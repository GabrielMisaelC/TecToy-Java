package br.com.tectoyautomacao.print_usb;

public class ESCUtil {

    public static final byte ESC = 0x1B;// Escape
    public static final byte FS =  0x1C;// Text delimiter
    public static final byte GS =  0x1D;// Group separator
    public static final byte DLE = 0x10;// data link escape
    public static final byte EOT = 0x04;// End of transmission
    public static final byte ENQ = 0x05;// Enquiry character
    public static final byte SP =  0x20;// Spaces
    public static final byte HT =  0x09;// Horizontal list
    public static final byte LF =  0x0A;//Print and wrap (horizontal orientation)
    public static final byte CR =  0x0D;// Home key
    public static final byte FF =  0x0C;// Carriage control (print and return to the standard mode (in page mode))
    public static final byte CAN = 0x18;// Canceled (cancel print data in page mode)


    //Inicialize a impressora
    public static byte[] init_printer() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 0x40;
        return result;
    }

    public static byte[] alignLeft() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        return result;
    }

    public static byte[] alignCenter() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        return result;
    }

    public static byte[] alignRight() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        return result;
    }

    // Print and return to Standard mode (in Page mode)
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=12
    public static byte[] pageMode(){
        byte[] result = new byte[1];
        result[0] = FF;
        return result;
    }

    // Select Page mode
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=193#esc_cl
    public static byte[] selectPagemodeDefault(){
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 0x4C;
        return result;
    }

    // Print and carriage return
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=13
    public static byte[] printCarriageReturn(){
        byte[] result = new byte[1];
        result[0] = GS;
        return result;
    }

    // Transmit real-time status
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=118
    public static byte[] transmitRealTimeStatus(int status){
        byte[] result = new byte[3];
        result[0] = DLE;
        result[1] = 0x04;
        result[2] = (byte) status;
        return result;
    }

    // Print data in Page mode
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=14
    public static byte[] printDataInPageMode(int status){
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x4D;
        result[2] = (byte) status;
        return result;
    }

//    // Print data in Page mode
//    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=23
//    public static byte[] printDataInPageMode(){
//        byte[] result = new byte[2];
//        result[0] = FF;
//        result[1] = 0x12;
//        return result;
//    }

    // Print data in Page mode
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=27#esc_cm
    public static byte[] selectCharacterFont(int mode){
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 0x12;
        return result;
    }

    public static byte[] feedPaper(int lines) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 0x64;
        result[2] = (byte) lines ;
        return result;
    }


}
