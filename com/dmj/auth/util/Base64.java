package com.dmj.auth.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/* loaded from: Base64.class */
public class Base64 {
    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] IA = new int[256];

    static {
        Arrays.fill(IA, -1);
        int iS = CA.length;
        for (int i = 0; i < iS; i++) {
            IA[CA[i]] = i;
        }
        IA[61] = 0;
    }

    public static final char[] encodeToChar(byte[] sArr, boolean lineSep) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new char[0];
        }
        int eLen = (sLen / 3) * 3;
        int cCnt = (((sLen - 1) / 3) + 1) << 2;
        int dLen = cCnt + (lineSep ? ((cCnt - 1) / 76) << 1 : 0);
        char[] dArr = new char[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < eLen) {
            int i = s;
            int s2 = s + 1;
            int s3 = s2 + 1;
            int i2 = ((sArr[i] & 255) << 16) | ((sArr[s2] & 255) << 8);
            s = s3 + 1;
            int i3 = i2 | (sArr[s3] & 255);
            int i4 = d;
            int d2 = d + 1;
            dArr[i4] = CA[(i3 >>> 18) & 63];
            int d3 = d2 + 1;
            dArr[d2] = CA[(i3 >>> 12) & 63];
            int d4 = d3 + 1;
            dArr[d3] = CA[(i3 >>> 6) & 63];
            d = d4 + 1;
            dArr[d4] = CA[i3 & 63];
            if (lineSep) {
                cc++;
                if (cc == 19 && d < dLen - 2) {
                    int d5 = d + 1;
                    dArr[d] = '\r';
                    d = d5 + 1;
                    dArr[d5] = '\n';
                    cc = 0;
                }
            }
        }
        int left = sLen - eLen;
        if (left > 0) {
            int i5 = ((sArr[eLen] & 255) << 10) | (left == 2 ? (sArr[sLen - 1] & 255) << 2 : 0);
            dArr[dLen - 4] = CA[i5 >> 12];
            dArr[dLen - 3] = CA[(i5 >>> 6) & 63];
            dArr[dLen - 2] = left == 2 ? CA[i5 & 63] : '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }

    public static final byte[] decode(char[] sArr) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; i++) {
            if (IA[sArr[i]] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i2 = sLen;
        while (i2 > 1) {
            i2--;
            if (IA[sArr[i2]] > 0) {
                break;
            }
            if (sArr[i2] == '=') {
                pad++;
            }
        }
        int len = (((sLen - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int i3 = 0;
            int j = 0;
            while (j < 4) {
                int i4 = s;
                s++;
                int c = IA[sArr[i4]];
                if (c >= 0) {
                    i3 |= c << (18 - (j * 6));
                } else {
                    j--;
                }
                j++;
            }
            int i5 = d;
            d++;
            dArr[i5] = (byte) (i3 >> 16);
            if (d < len) {
                d++;
                dArr[d] = (byte) (i3 >> 8);
                if (d < len) {
                    d++;
                    dArr[d] = (byte) i3;
                }
            }
        }
        return dArr;
    }

    public static final byte[] decodeFast(char[] sArr) {
        int i;
        int sLen = sArr.length;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && IA[sArr[sIx]] < 0) {
            sIx++;
        }
        while (eIx > 0 && IA[sArr[eIx]] < 0) {
            eIx--;
        }
        int pad = sArr[eIx] == '=' ? sArr[eIx - 1] == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            i = (sArr[76] == '\r' ? cCnt / 78 : 0) << 1;
        } else {
            i = 0;
        }
        int sepCnt = i;
        int len = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        int eLen = (len / 3) * 3;
        while (d < eLen) {
            int i2 = sIx;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int i3 = (IA[sArr[i2]] << 18) | (IA[sArr[sIx2]] << 12);
            int sIx4 = sIx3 + 1;
            int i4 = i3 | (IA[sArr[sIx3]] << 6);
            sIx = sIx4 + 1;
            int i5 = i4 | IA[sArr[sIx4]];
            int i6 = d;
            int d2 = d + 1;
            dArr[i6] = (byte) (i5 >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i5 >> 8);
            d = d3 + 1;
            dArr[d3] = (byte) i5;
            if (sepCnt > 0) {
                cc++;
                if (cc == 19) {
                    sIx += 2;
                    cc = 0;
                }
            }
        }
        if (d < len) {
            int i7 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                int i8 = sIx;
                sIx++;
                i7 |= IA[sArr[i8]] << (18 - (j * 6));
                j++;
            }
            int r = 16;
            while (d < len) {
                int i9 = d;
                d++;
                dArr[i9] = (byte) (i7 >> r);
                r -= 8;
            }
        }
        return dArr;
    }

    public static final byte[] encodeToByte(byte[] sArr, boolean lineSep) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int eLen = (sLen / 3) * 3;
        int cCnt = (((sLen - 1) / 3) + 1) << 2;
        int dLen = cCnt + (lineSep ? ((cCnt - 1) / 76) << 1 : 0);
        byte[] dArr = new byte[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < eLen) {
            int i = s;
            int s2 = s + 1;
            int s3 = s2 + 1;
            int i2 = ((sArr[i] & 255) << 16) | ((sArr[s2] & 255) << 8);
            s = s3 + 1;
            int i3 = i2 | (sArr[s3] & 255);
            int i4 = d;
            int d2 = d + 1;
            dArr[i4] = (byte) CA[(i3 >>> 18) & 63];
            int d3 = d2 + 1;
            dArr[d2] = (byte) CA[(i3 >>> 12) & 63];
            int d4 = d3 + 1;
            dArr[d3] = (byte) CA[(i3 >>> 6) & 63];
            d = d4 + 1;
            dArr[d4] = (byte) CA[i3 & 63];
            if (lineSep) {
                cc++;
                if (cc == 19 && d < dLen - 2) {
                    int d5 = d + 1;
                    dArr[d] = 13;
                    d = d5 + 1;
                    dArr[d5] = 10;
                    cc = 0;
                }
            }
        }
        int left = sLen - eLen;
        if (left > 0) {
            int i5 = ((sArr[eLen] & 255) << 10) | (left == 2 ? (sArr[sLen - 1] & 255) << 2 : 0);
            dArr[dLen - 4] = (byte) CA[i5 >> 12];
            dArr[dLen - 3] = (byte) CA[(i5 >>> 6) & 63];
            dArr[dLen - 2] = left == 2 ? (byte) CA[i5 & 63] : (byte) 61;
            dArr[dLen - 1] = 61;
        }
        return dArr;
    }

    public static final byte[] decode(byte[] sArr) {
        int sLen = sArr.length;
        int sepCnt = 0;
        for (byte b : sArr) {
            if (IA[b & 255] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i = sLen;
        while (i > 1) {
            i--;
            if (IA[sArr[i] & 255] > 0) {
                break;
            }
            if (sArr[i] == 61) {
                pad++;
            }
        }
        int len = (((sLen - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int i2 = 0;
            int j = 0;
            while (j < 4) {
                int i3 = s;
                s++;
                int c = IA[sArr[i3] & 255];
                if (c >= 0) {
                    i2 |= c << (18 - (j * 6));
                } else {
                    j--;
                }
                j++;
            }
            int i4 = d;
            d++;
            dArr[i4] = (byte) (i2 >> 16);
            if (d < len) {
                d++;
                dArr[d] = (byte) (i2 >> 8);
                if (d < len) {
                    d++;
                    dArr[d] = (byte) i2;
                }
            }
        }
        return dArr;
    }

    public static final byte[] decodeFast(byte[] sArr) {
        int i;
        int sLen = sArr.length;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && IA[sArr[sIx] & 255] < 0) {
            sIx++;
        }
        while (eIx > 0 && IA[sArr[eIx] & 255] < 0) {
            eIx--;
        }
        int pad = sArr[eIx] == 61 ? sArr[eIx - 1] == 61 ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            i = (sArr[76] == 13 ? cCnt / 78 : 0) << 1;
        } else {
            i = 0;
        }
        int sepCnt = i;
        int len = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        int eLen = (len / 3) * 3;
        while (d < eLen) {
            int i2 = sIx;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int i3 = (IA[sArr[i2]] << 18) | (IA[sArr[sIx2]] << 12);
            int sIx4 = sIx3 + 1;
            int i4 = i3 | (IA[sArr[sIx3]] << 6);
            sIx = sIx4 + 1;
            int i5 = i4 | IA[sArr[sIx4]];
            int i6 = d;
            int d2 = d + 1;
            dArr[i6] = (byte) (i5 >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i5 >> 8);
            d = d3 + 1;
            dArr[d3] = (byte) i5;
            if (sepCnt > 0) {
                cc++;
                if (cc == 19) {
                    sIx += 2;
                    cc = 0;
                }
            }
        }
        if (d < len) {
            int i7 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                int i8 = sIx;
                sIx++;
                i7 |= IA[sArr[i8]] << (18 - (j * 6));
                j++;
            }
            int r = 16;
            while (d < len) {
                int i9 = d;
                d++;
                dArr[i9] = (byte) (i7 >> r);
                r -= 8;
            }
        }
        return dArr;
    }

    public static final String encodeToString(byte[] sArr, boolean lineSep) {
        return new String(encodeToChar(sArr, lineSep));
    }

    public static final String encode(String s) {
        try {
            return new String(encodeToChar(s.getBytes("UTF-8"), false));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] decode(String str, boolean used) {
        int sLen = str != null ? str.length() : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; i++) {
            if (IA[str.charAt(i)] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i2 = sLen;
        while (i2 > 1) {
            i2--;
            if (IA[str.charAt(i2)] > 0) {
                break;
            }
            if (str.charAt(i2) == '=') {
                pad++;
            }
        }
        int len = (((sLen - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int i3 = 0;
            int j = 0;
            while (j < 4) {
                int i4 = s;
                s++;
                int c = IA[str.charAt(i4)];
                if (c >= 0) {
                    i3 |= c << (18 - (j * 6));
                } else {
                    j--;
                }
                j++;
            }
            int i5 = d;
            d++;
            dArr[i5] = (byte) (i3 >> 16);
            if (d < len) {
                d++;
                dArr[d] = (byte) (i3 >> 8);
                if (d < len) {
                    d++;
                    dArr[d] = (byte) i3;
                }
            }
        }
        return dArr;
    }

    public static final byte[] decodeFast(String s) {
        int i;
        int sLen = s.length();
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && IA[s.charAt(sIx) & 255] < 0) {
            sIx++;
        }
        while (eIx > 0 && IA[s.charAt(eIx) & 255] < 0) {
            eIx--;
        }
        int pad = s.charAt(eIx) == '=' ? s.charAt(eIx - 1) == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            i = (s.charAt(76) == '\r' ? cCnt / 78 : 0) << 1;
        } else {
            i = 0;
        }
        int sepCnt = i;
        int len = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        int eLen = (len / 3) * 3;
        while (d < eLen) {
            int i2 = sIx;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int i3 = (IA[s.charAt(i2)] << 18) | (IA[s.charAt(sIx2)] << 12);
            int sIx4 = sIx3 + 1;
            int i4 = i3 | (IA[s.charAt(sIx3)] << 6);
            sIx = sIx4 + 1;
            int i5 = i4 | IA[s.charAt(sIx4)];
            int i6 = d;
            int d2 = d + 1;
            dArr[i6] = (byte) (i5 >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i5 >> 8);
            d = d3 + 1;
            dArr[d3] = (byte) i5;
            if (sepCnt > 0) {
                cc++;
                if (cc == 19) {
                    sIx += 2;
                    cc = 0;
                }
            }
        }
        if (d < len) {
            int i7 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                int i8 = sIx;
                sIx++;
                i7 |= IA[s.charAt(i8)] << (18 - (j * 6));
                j++;
            }
            int r = 16;
            while (d < len) {
                int i9 = d;
                d++;
                dArr[i9] = (byte) (i7 >> r);
                r -= 8;
            }
        }
        return dArr;
    }

    public static String decode(String s) throws UnsupportedEncodingException {
        return new String(decodeFast(s), "UTF-8");
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        encode("��");
    }
}
