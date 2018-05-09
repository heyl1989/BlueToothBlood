package com.blue.blueapplication.utils;



public class OADImgHdr {

	short crc0;
	short crc1;
	short ver;
	int len;
	byte[] uid = new byte[4];
	short addr;
	byte imgType;

	/**
	 *
	 * @param buf 文件字节数组
	 * @param len 文件数组长度
     */
	public OADImgHdr(byte[] buf, int len) {
		this.len = len / 4;// 32*4K=128�ֽڣ�32*0x1000/(16/4)��
		this.ver = 0;
		this.uid[0] = this.uid[1] = this.uid[2] = this.uid[3] = 'E';
		this.addr = 0x400;
		this.imgType = 1; // EFL_OAD_IMG_TYPE_APP
		this.crc0 = calcImageCRC((int) 0, buf);
		crc1 = (short) 0xFFFF;
	}

	public byte[] getRequest() {
		byte[] tmp = new byte[16];
		tmp[0] = BCDToInt.loUint16((short) this.crc0);
		tmp[1] = BCDToInt.hiUint16((short) this.crc0);
		tmp[2] = BCDToInt.loUint16((short) this.crc1);
		tmp[3] = BCDToInt.hiUint16((short) this.crc1);
		tmp[4] = BCDToInt.loUint16(this.ver);
		tmp[5] = BCDToInt.hiUint16(this.ver);
		tmp[6] = BCDToInt.loUint16((short) this.len);
		tmp[7] = BCDToInt.hiUint16((short) this.len);
		tmp[8] = tmp[9] = tmp[10] = tmp[11] = this.uid[0];
		tmp[12] = BCDToInt.loUint16(this.addr);
		tmp[13] = BCDToInt.hiUint16(this.addr);
		tmp[14] = imgType;
		tmp[15] = (byte) 0xFF;
		return tmp;
	}

	short calcImageCRC(int page, byte[] buf) {
		short crc = 0;
		long addr = page * 0x1000;

		byte pageBeg = (byte) page;
		byte pageEnd = (byte) (this.len / (0x1000 / 4));
		// һ�����ȣ��ĸ��ֽ�
		int osetEnd = ((this.len - (pageEnd * (0x1000 / 4))) * 4);

		pageEnd += pageBeg;

		while (true) {
			int oset;

			for (oset = 0; oset < 0x1000; oset++) {
				if ((page == pageBeg) && (oset == 0x00)) {
					// Skip the CRC and shadow.
					// Note: this increments by 3 because oset is
					// incremented by 1 in each pass
					// through the loop
					oset += 3;
				} else if ((page == pageEnd) && (oset == osetEnd)) {
					crc = this.crc16(crc, (byte) 0x00);
					crc = this.crc16(crc, (byte) 0x00);

					return crc;
				} else {
					crc = this.crc16(crc, buf[(int) (addr + oset)]);
				}
			}
			page += 1;
			addr = page * 0x1000;
		}

	}

	short crc16(short crc, byte val) {
		final int poly = 0x1021;
		byte cnt;
		for (cnt = 0; cnt < 8; cnt++, val <<= 1) {
			byte msb;
			if ((crc & 0x8000) == 0x8000) {
				msb = 1;
			} else
				msb = 0;

			crc <<= 1;
			if ((val & 0x80) == 0x80) {
				crc |= 0x0001;
			}
			if (msb == 1) {
				crc ^= poly;
			}
		}

		return crc;
	}

}
