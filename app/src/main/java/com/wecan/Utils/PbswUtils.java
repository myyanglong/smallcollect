package com.wecan.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by xiaotaozhu on 2017/7/6.
 */

public class PbswUtils {
    static final byte UDLOAD_HD_SFLAG = 0x68;
    static final byte UDLOAD_HD_EFLAG = 0x16;
    static final byte UDLOAD_HD_MVER = 0x07;
    static final byte UDLOAD_HD_SVER = 0x01;
    static final byte FUNCODE = 0x02;
    static final int  MINFRAMESIZE = 11;
    static final int UD_DEVID_SIZE = 4;
    static final int UD_TAG_SIZE = 1;
    static final int UD_TAG_FF_DSIZE = 6;

    static final byte UDLOAD_HD_AFN_2SERVER			= (byte)0x1;	/* dev send data to server */
    static final byte UDLOAD_HD_AFN_2DEV_NOTP		= (byte)0x81; /* rsp from platform with not parameter */
    static final byte UDLOAD_HD_AFN_2DEV_HASP		= (byte)0xc1; /* rsp from platform with parameter */
    static final byte UDLOAD_HD_AFN_DEV_ASKRETRY	= (byte)0x02;	/* dev to platform retransmit frames */
    static final byte UDLOAD_HD_AFN_SERVER_ASKRETRY	= (byte)0x82;	/* server ask device retransmit frames */
    static final int UDLOAD_HD_AFN_INVALIDAE = -1;

    public static int getselfaddr() {
        return 0;
    }

    public static int getwid(byte[] d, int offset) {
        int wid = ((d[offset] & 0xff) <<24) | ((d[offset+1] & 0xff) << 16) | ((d[offset+2] & 0xff) << 8) | (d[offset+3] & 0xff);
        return wid;
    }

    public static byte chksum(byte[] d, int len) {
        byte sum = 0;
        for (int i=0; i<len; i++) {
            sum += d[i];
        }

        return sum;
    }

    /**
     *
     * @param fir 起
     * @param fin 始
     * @param frameno 帧长度
     * @param d 数据
     * @return
     */
    public static byte[] encode(boolean fir, boolean fin, int frameno, byte[] d) {
        int fcs = 0;
        if ( fir ) {
            fcs |= 0x1<<15;
        }
        if ( fin ) {
            fcs |= 0x1<<14;
        }
        fcs |= frameno;
        ByteBuffer bb = ByteBuffer.allocate(1024);
        bb.order(ByteOrder.BIG_ENDIAN);
        // fill hd
        bb.put( UDLOAD_HD_SFLAG );
        bb.putShort( (short)(d.length  + MINFRAMESIZE) );
        bb.put( UDLOAD_HD_SFLAG );
        bb.put( UDLOAD_HD_MVER );
        bb.put( UDLOAD_HD_SVER );
        bb.put( FUNCODE );
        bb.putShort( (short)fcs );

        // fill data
        bb.put(d);

        bb.put((byte) 0);   // simulate tail
        bb.put((byte) 0);

        bb.flip();

        // fill tail
        int tlen = bb.limit();
        byte[] ba = new byte[tlen];
        bb.get(ba);
        ba[tlen-2] = chksum(ba, tlen-2);
        ba[tlen-1] = UDLOAD_HD_EFLAG;

        return ba;
    }

    public static int udload_chk_down_vaildate(byte[] d) {
        byte fncode = 0;

		/* 1: check min length */
        if ( d.length < MINFRAMESIZE ) {
            return UDLOAD_HD_AFN_INVALIDAE;
        }

		/* 2: check end flag & checksum */
        if ( UDLOAD_HD_EFLAG != d[d.length-1] ) {
            return UDLOAD_HD_AFN_INVALIDAE;
        }

        if ( chksum(d, d.length-1) != d[d.length-1] ) {
            return UDLOAD_HD_AFN_INVALIDAE;
        }

		/* 3: check HD section */
        int dlen = ((d[1]*256) & 0xffff) + (d[1] & 0xff);
        if ( UDLOAD_HD_SFLAG!=d[0] || UDLOAD_HD_SFLAG!=d[3] ||
                UDLOAD_HD_MVER!=d[4] || UDLOAD_HD_SVER!=d[5] || dlen != d.length ) {
            return UDLOAD_HD_AFN_INVALIDAE;
        }

        fncode = d[6];
        if ( !(UDLOAD_HD_AFN_2DEV_HASP==fncode || UDLOAD_HD_AFN_2DEV_NOTP==fncode || UDLOAD_HD_AFN_SERVER_ASKRETRY==fncode) ) {
            return UDLOAD_HD_AFN_INVALIDAE;
        }

        return fncode;
    }

    public static int udload_down_kind(byte[] d) {
        int flag = 0;
        int funcode = udload_chk_down_vaildate(d);
        if ( UDLOAD_HD_AFN_INVALIDAE==funcode || getselfaddr()!=getwid(d, 9) ) {
            return 0;
        }

        int dlen = d.length - MINFRAMESIZE;
		/* seek to content, compare dev id */
        switch ( funcode ) {
//			case UDLOAD_HD_AFN_SERVER_ASKRETRY:	{
//				/* A: devid(4B), TAG(1B)--0x01, BITMAPS(128B)--lsb order */
//				if ( (UD_DEVID_SIZE+UD_TAG_SIZE+UDLOAD_MAX_BITMAP) == dlen ) {
//					if ( UD_TAG_82_01==pconent[UD_DEVID_SIZE] ) {
//						DEBUG_PRINT(DBGLV_PROMOT, ("udload_down_kind UDLOAD_HD_AFN_SERVER_ASKRETRY!\r\n"));
//						memcpy(bitmaps, &pconent[UD_DEVID_SIZE+UD_TAG_SIZE], UDLOAD_MAX_BITMAP);
//						return 1;
//					} else {
//						DEBUG_PRINT(DBGLV_PROMOT, ("udload_down_kind UDLOAD_HD_AFN_SERVER_ASKRETRY tag error!\r\n"));
//					}
//				}  else {
//					DEBUG_PRINT(DBGLV_PROMOT, ("udload_down_kind UDLOAD_HD_AFN_SERVER_ASKRETRY len error!\r\n"));
//					flag = false;
//				}
//			}
//				break;
//
            case UDLOAD_HD_AFN_2DEV_NOTP: {
				/* B: item 4+1+6=11B, devid(4B), tag(1B)--0xff, ymdhms(6B) */
                if ( 0!=dlen && (UD_DEVID_SIZE+UD_TAG_SIZE+UD_TAG_FF_DSIZE)!=dlen ) {
                    //DEBUG_PRINT(DBGLV_PROMOT, ("udload_down_kind UDLOAD_HD_AFN_2DEV_NOTP len error!\r\n"));
                } else {
                    flag = 1;
                }
            }
            break;

            case UDLOAD_HD_AFN_2DEV_HASP: {
				/* C: process response with parameter */
//				if ( true != udload_down_c1(pconent, inlen-(sizeof(UDHD) + sizeof(UDTAIL))) ) {
//					flag = false;
//					DEBUG_PRINT(DBGLV_PROMOT, ("udload_down_kind UDLOAD_HD_AFN_2DEV_HASP parse error!\r\n"));
//				} else {
//					DEBUG_PRINT(DBGLV_PROMOT, ("udload_down_kind UDLOAD_HD_AFN_2DEV_HASP parse OK!\r\n"));
//				}
            }
            flag = 1;
            break;

            default:
                flag = 0;
        }

        return flag;
    }
}
