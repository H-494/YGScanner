package invonate.cn.ygscanner.Entry;

import java.util.List;

public class Kkwcx {

    /**
     * formId : SHHL61
     * result : S
     * msg : 成功
     * data : [{"WAREHOUSENO":"B2W","ARENO":"001","WARENO":"B2WA"},{"WAREHOUSENO":"B2W","ARENO":"002","WARENO":"B2WA"},{"WAREHOUSENO":"B2W","ARENO":"003","WARENO":"B2WA"},{"WAREHOUSENO":"B2W","ARENO":"001","WARENO":"B2WC"},{"WAREHOUSENO":"B2W","ARENO":"001","WARENO":"B2WD"},{"WAREHOUSENO":"B2W","ARENO":"002","WARENO":"B2WD"},{"WAREHOUSENO":"B2W","ARENO":"003","WARENO":"B2WD"},{"WAREHOUSENO":"B2W","ARENO":"001","WARENO":"B2WE"}]
     * endFlag : *
     */

    private String formId;
    private String result;
    private String msg;
    private String endFlag;
    private List<DataBean> data;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(String endFlag) {
        this.endFlag = endFlag;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * WAREHOUSENO : B2W
         * ARENO : 001
         * WARENO : B2WA
         */

        private String WAREHOUSENO;
        private String ARENO;
        private String WARENO;

        public String getWAREHOUSENO() {
            return WAREHOUSENO;
        }

        public void setWAREHOUSENO(String WAREHOUSENO) {
            this.WAREHOUSENO = WAREHOUSENO;
        }

        public String getARENO() {
            return ARENO;
        }

        public void setARENO(String ARENO) {
            this.ARENO = ARENO;
        }

        public String getWARENO() {
            return WARENO;
        }

        public void setWARENO(String WARENO) {
            this.WARENO = WARENO;
        }
    }
}
