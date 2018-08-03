package invonate.cn.ygscanner.Entry;

import java.util.List;

public class Kwcc {

    /**
     * formId : SHHL31L
     * result : S
     * msg : 成功
     * data : [{"key":"卷号","value":"17E10519320"},{"key":"产品形态代码","value":""},{"key":"轧制批号","value":"17E105193B"},{"key":"断面类型","value":""},{"key":"长度","value":"6700"},{"key":"钢材重量","value":"2584"},{"key":"检斤重量","value":""},{"key":"库","value":"B03"},{"key":"库位序列号","value":""},{"key":"区","value":"EF72-5"},{"key":"排","value":""},{"key":"排","value":""},{"key":"前库","value":"B04"},{"key":"前库位序列号","value":""},{"key":"前区","value":"*"},{"key":"前排","value":""}]
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
         * key : 卷号
         * value : 17E10519320
         */

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
