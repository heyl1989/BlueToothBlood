/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blue.blueapplication.activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.blue.blueapplication.cache.SharePCach;
import com.blue.blueapplication.utils.BCDToInt;
import com.blue.blueapplication.utils.FirmFileUtil;
import com.blue.blueapplication.utils.OADImgHdr;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = "service";

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	/**
	 * OAD
	 */
	public static final String OAD_SERVER = "oad_server";
	public static final UUID OAD_SERVICE_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
	public static final UUID OAD_IDENTI_UUID = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
	public static final UUID OAD_BLOCK_UUID = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");

	private byte[] oadBuffer = null;
	private int oadPackage = 0;
	byte[] oadPackageByte = new byte[18];


	/**
	 * 蓝牙回调
	 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			System.out.println("=======status:" + status);
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG,"onCharacteristicRead==============="+characteristic.getUuid());
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {

			Log.i(TAG,"onDescriptorWriteonDescriptorWrite = " + status
					+ ", descriptor =" + descriptor.getUuid().toString());
			if (BluetoothGatt.GATT_SUCCESS == status) {
				UUID uuid = descriptor.getCharacteristic().getUuid();
				if (uuid.equals(OAD_BLOCK_UUID)) {
					// 设置notify监听成功,写入固件信息包
					byte[] value = descriptor.getCharacteristic().getValue();
					System.out.println("写入固件信息包：" + Arrays.toString(value));
					writeCharacteristic(OAD_SERVICE_UUID, OAD_IDENTI_UUID, value);
				}

			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			if (characteristic.getValue() != null) {

				Log.i(TAG,"data====="+characteristic.getStringValue(0));
			}
			Log.i(TAG,"--------onCharacteristicChanged-----");
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.i(TAG,"rssi = " + rssi);
		}

		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG,"--------write success----- status:" + status);
			UUID uuidString = characteristic.getUuid();
			if (uuidString.equals(OAD_BLOCK_UUID)) {
				if (status == 0) {
					try {
						Thread.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int allPakeage = oadBuffer.length / 16;
					oadPackage++;
					if (oadPackage < allPakeage) {
						// broadcastUpdate(OAD_PROGRESS, oadPackage);
						String progress = "固件升级进行中：" + oadPackage + "/" + allPakeage;
						System.out.println(progress);
						updateOadProgress(progress,oadPackage,allPakeage);
						oadPackageByte[0] = BCDToInt.loUint16((short) oadPackage);
						oadPackageByte[1] = BCDToInt.hiUint16((short) oadPackage);
						System.arraycopy(oadBuffer, oadPackage * 16, oadPackageByte, 2, 16);
						writeCharacteristic(OAD_SERVICE_UUID, OAD_BLOCK_UUID, oadPackageByte);
						if (oadPackage == allPakeage - 1) {
							System.out.println("固件升级结束！");
							updateOadProgress("固件升级结束！",oadPackage,allPakeage);
						}
					}
				}
			}

		};
	};

	private void updateOadProgress(String str,int oldPackage,int allPackage) {
		Intent intent = new Intent(UpdateFirmwareActivity.OAD_PROGRESS);
		intent.putExtra("progress", str);
		intent.putExtra("oldPackage", oldPackage);
		intent.putExtra("allPackage", allPackage);
		LocalBroadcastManager.getInstance(BluetoothLeService.this).sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		Log.i("蓝牙服务GATT协议的UUID",""+characteristic.getUuid());
		Log.i("蓝牙服务GATT协议的Properties",""+characteristic.getProperties());
		Log.i("蓝牙服务GATT协议的返回数据", ""+Arrays.toString(characteristic.getValue()));
		//Log.i("蓝牙服务GATT协议的年",""+bytesToInt2(characteristic.getValue(),2));
		UUID uuidString = characteristic.getUuid();
		if ("f0080002-0451-4000-b000-000000000000".equals(characteristic.getUuid().toString())) {
			synchronized (BluetoothLeService.class) {

				byte[] data = characteristic.getValue();
				if(data[0] == -112){
					int index = 0;
					String head = data[0]+"";
					String height = "0";
					String low = "0";
					String heartRate = "0";
					String process = "0";
					for (int i = 0; i < data.length; i++) {
						if (i == 1) {
							height = String.format("%02d", data[i]);
						} else if (i == 2) {
							low = String.format("%02d", data[i]);
						} else if (i == 3) {
							process = String.format("%02d", data[i]);
						} else if (i == 5) {
							heartRate = String.format("%02d",data[i]);
						}
					}
					int heightTemp = Integer.parseInt(height);
					int lowTemp = Integer.parseInt(low);
					int heartRateTemp = Integer.parseInt(heartRate);
					if (heightTemp<0){
						int m = 256+heightTemp;
						height = String.valueOf(m);
					}
					if (lowTemp<0){
						int m = 256+lowTemp;
						low = String.valueOf(m);
					}
					if (heartRateTemp<0){
						int m = 256+heartRateTemp;
						heartRate = String.valueOf(m);
					}

					Log.i(TAG, height + " " + low + " " + heartRate+" "+process);

					intent.putExtra(EXTRA_DATA, head + "d"+height + "d" + low + "d" + heartRate+ "d" + process);
				}else if(data[0] == -25){
					String date = bytesToInt2(data,2) + "-"+String.format("%02d",data[4])
							+"-" + String.format("%02d",data[5]) + " " +String.format("%02d",data[6])
							+ ":" + String.format("%02d",data[7])+":00";
					Log.i("DATE",date+"");
					String height = data[8]+"";
					String low = data[9]+"";
					String heartRate = data[10]+"";
					String head = data[0]+"";
					String num = data[1]+"";

					int heightTemp = Integer.parseInt(height);
					int lowTemp = Integer.parseInt(low);
					int heartRateTemp = Integer.parseInt(heartRate);
					if (heightTemp<0){
						int m = 256+heightTemp;
						height = String.valueOf(m);
					}
					if (lowTemp<0){
						int m = 256+lowTemp;
						low = String.valueOf(m);
					}
					if (heartRateTemp<0){
						int m = 256+heartRateTemp;
						heartRate = String.valueOf(m);
					}
					intent.putExtra(EXTRA_DATA, head +","+num+","+date+","+height+","+low+","+heartRate);
				}else if(data[0] == 90){
					String  sdata = Arrays.toString(data).replace("[","").replace("]","");
					intent.putExtra(EXTRA_DATA, sdata+"");
				}else if(data[0] == -80){
					String  sdata = Arrays.toString(data).replace("[","").replace("]","");
					intent.putExtra(EXTRA_DATA, sdata+"");
				}else if(data[0] == -94){
					String  sdata = Arrays.toString(data).replace("[","").replace("]","");
					intent.putExtra(EXTRA_DATA, sdata+"");
				}else if(data[0] == -92){
					String  sdata = data[0]+","+ data[1] + "";
					intent.putExtra(EXTRA_DATA, sdata+"");
				}else if(data[0] == -104){
					String  sdata = Arrays.toString(data).replace("[","").replace("]","");
					intent.putExtra(EXTRA_DATA, sdata+"");
				}

			}
			sendBroadcast(intent);
		}
		//固件升级第一包
		else if (uuidString.equals(OAD_BLOCK_UUID)) {
			byte value[] = characteristic.getValue();
			System.out.println("固件信息拿到返回包" + Arrays.toString(value));
			if (value.length == 2 && value[0] == (byte) 0x00 && value[1] == (byte) 0x00) {
				sendOADFirstPackage();
			}
		}
		else{
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02d ", byteChar));

				try {
					Log.i(TAG,"ppp" + new String(data,"gbk") + "\n"
							+ stringBuilder.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				intent.putExtra(EXTRA_DATA, new String(data) + "\n"
						+ stringBuilder.toString());
			}
			sendBroadcast(intent);
		}

	}

	/**
	 * 固件升级第一包
	 */
	private void sendOADFirstPackage() {
		oadPackage = 0;
		oadPackageByte[0] = BCDToInt.loUint16((short) oadPackage);
		oadPackageByte[1] = BCDToInt.hiUint16((short) oadPackage);
		System.arraycopy(oadBuffer, 0, oadPackageByte, 2, 16);
		writeCharacteristic(OAD_SERVICE_UUID, OAD_BLOCK_UUID, oadPackageByte);
	}

	/**
	 * 固件升级写入数据
	 * @param service_UUID
	 * @param config_UUID
	 * @param value
     */
	public void writeCharacteristic(UUID service_UUID, UUID config_UUID, byte[] value) {
		if (null == mBluetoothGatt) {
		}

		BluetoothGattService gattService = mBluetoothGatt.getService(service_UUID);
		if (null == gattService) {
			return;
		}

		BluetoothGattCharacteristic RxChar = gattService.getCharacteristic(config_UUID);
		if (RxChar == null) {

			return;
		}
		RxChar.setValue(value);
		boolean status = mBluetoothGatt.writeCharacteristic(RxChar);

	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
	 */
	private int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (int) ( ((src[offset] & 0xFF)<<8)
				|(src[offset+1] & 0xFF));
		return value;
	}
	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
	 */
	public static int bytesToInt(byte[] src, int offset) {
		int value;
		int a = src[offset];
		int b = src[offset+1];
		if(a < 0){
			a = src[offset]+256;
		}
		if(b < 0){
			b = src[offset+1]+256;
		}
		value = (int) ((a & 0xFF)
				| ((b & 0xFF)<<8));
		return value;
	}

	public class LocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		/***
		 * 服务解绑的时候注销广播接受者
		 */
		LocalBroadcastManager.getInstance(this).unregisterReceiver(OADBroadcast);
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		LocalBroadcastManager.getInstance(this).registerReceiver(OADBroadcast, getFilter());
		return true;
	}

	/**
	 * 对广播进行过滤接受
	 * @return
     */
	private IntentFilter getFilter() {
		//
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(OAD_SERVER);
		return mIntentFilter;
	}
	/**
	 * 收广播
	 *
	 */
	private final BroadcastReceiver OADBroadcast = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == OAD_SERVER) {
				String mFilePath = SharePCach.loadStringCach("filePath");
				System.out.println("File path=" + mFilePath);
				File mFile = new File(mFilePath);
				if (mFile.exists()) {
					System.out.println("文件存在");
				} else {
					System.out.println("文件不存在");
					return;
				}
				oadBuffer = FirmFileUtil.getFileByte(mFilePath);
				setOADNotify();
			}

		}

	};

	/**
	 * 固件升级第二包数据处理
	 */
	private void setOADNotify() {
		OADImgHdr oad = new OADImgHdr(oadBuffer, oadBuffer.length);
		byte[] cmd = oad.getRequest();
		System.out.println("固件信息：" + Arrays.toString(cmd));
		enableNotification(OAD_SERVICE_UUID, OAD_BLOCK_UUID, cmd);

	}

	/**
	 * 固件升级第二包
	 * @param service_UUID
	 * @param read_UUID
	 * @param cmdHead
     */
	public void enableNotification(UUID service_UUID, UUID read_UUID, byte[] cmdHead) {

		if (mBluetoothGatt == null) {
			// LoggerUtil.i("mBluetoothGatt null" + mBluetoothGatt);
			return;
		}

		BluetoothGattService gattService = mBluetoothGatt.getService(service_UUID);
		if (null == gattService) {
			// LoggerUtil.d("Rx service not found! ·þÎñÕÒ²»µ½");
			return;
		}

		BluetoothGattCharacteristic TxChar = gattService.getCharacteristic(read_UUID);
		if (TxChar == null) {
			// LoggerUtil.e("Tx charateristic not found!");
			return;
		}
		TxChar.setValue(cmdHead);
		// LoggerUtil.d("sbluetooth ----Ð´Èënotionfication----");
		mBluetoothGatt.setCharacteristicNotification(TxChar, true);
		final UUID DEC_2 = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
		BluetoothGattDescriptor descriptor = TxChar.getDescriptor(DEC_2);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		boolean status = mBluetoothGatt.writeDescriptor(descriptor);
	}


	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || TextUtils.isEmpty(address)) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.writeCharacteristic(characteristic);

	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		if (descriptor != null) {
			Log.w(TAG,"write descriptor");
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	public void setCharacteristicUpdateNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(SampleGattAttributes.UPDATE_CHARACTERISTIC_CONFIG));
		Log.w("descriptor",descriptor+" ");
		if (descriptor != null) {
			Log.w(TAG,"write descriptor");
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}
	public boolean getRssiVal() {
		if (mBluetoothGatt == null)
			return false;

		return mBluetoothGatt.readRemoteRssi();
	}
}
