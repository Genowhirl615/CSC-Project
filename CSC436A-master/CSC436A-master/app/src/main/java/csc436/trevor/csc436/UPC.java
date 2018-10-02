package csc436.trevor.csc436;

public class UPC {
    private String barcode;
    private String name;

    public UPC() {};

    public UPC(String barcode, String name){
        this.barcode = barcode;
        this.name = name;
    }

    public String getBarcode(){
        return barcode;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setBarcode(String barcode){
        this.barcode = barcode;
    }
}
