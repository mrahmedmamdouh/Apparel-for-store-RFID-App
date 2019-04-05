package co.kr.bluebird.rfid.app.bbrfiddemo;

public class repl_elements {

    public int mIv;
    public String mDt;
    public String StockItems;
    public int mDupCount;


    public boolean mHasPc;

    public Object getItems() {
        return items;
    }
    public Object  getStockItems() {
        return StockItems;
    }
    public void setStockItems(String  StockItems) {
        this.StockItems = StockItems;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String items;
    public String it_id;
    public String style;
    public String color;
    public String size;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String _id;

    public repl_elements() {

    }

    public String getIt_id() {
        return it_id;
    }

    public void setIt_id(String it_id) {
        this.it_id = it_id;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public repl_elements(String it_id, String style, String color, String size, String _id, String items, String  StockItems) {

        this.items = items;
        this.it_id = it_id;
        this.style = style;
        this.color = color;
        this.size = size;
        this._id=_id;
        this.StockItems = StockItems;

    }
}
