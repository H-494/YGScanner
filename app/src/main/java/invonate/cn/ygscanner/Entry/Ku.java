package invonate.cn.ygscanner.Entry;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Ku implements Serializable {


    /**
     * formId : SHHL31L
     * result : S
     * msg : 登陆成功
     * data : [{"chehao":[{"Value":"001"},{"Value":"002"}],"kubie":[{"name":"B20","ku":[{"name":"B20A","qv":[{"value":"001"},{"value":"002"},{"value":"003"}]},{"name":"B20B","qv":[{"value":"001"},{"value":"002"},{"value":"003"}]}]},{"name":"B30","ku":[{"name":"B30A","qv":[{"value":"001"},{"value":"002"},{"value":"003"}]},{"name":"B30B","qv":[{"value":"001"},{"value":"002"},{"value":"003"}]}]}]}]
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

    public static class DataBean implements Serializable{
        private List<ChehaoBean> chehao;
        private List<KubieBean> kubie;

        public List<ChehaoBean> getChehao() {
            return chehao;
        }

        public void setChehao(List<ChehaoBean> chehao) {
            this.chehao = chehao;
        }

        public List<KubieBean> getKubie() {
            return kubie;
        }

        public void setKubie(List<KubieBean> kubie) {
            this.kubie = kubie;
        }

        public static class ChehaoBean implements Serializable{
            @Override
            public String toString() {
                return Value;
            }

            /**
             * Value : 001
             */

            private String Value;

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }
        }

        public static class KubieBean implements Serializable{

            /**
             * name : B20
             * ku : [{"name":"B20A","qv":[{"value":"001"},{"value":"002"},{"value":"003"}]},{"name":"B20B","qv":[{"value":"001"},{"value":"002"},{"value":"003"}]}]
             */
            @Override
            public String toString() {
                return name;
            }

            private String name;
            private List<KuBean> ku;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<KuBean> getKu() {
                return ku;
            }

            public void setKu(List<KuBean> ku) {
                this.ku = ku;
            }

            public static class KuBean implements Serializable{
                /**
                 * name : B20A
                 * qv : [{"value":"001"},{"value":"002"},{"value":"003"}]
                 */

                @Override
                public String toString() {
                    return name;
                }

                private String name;
                private List<QvBean> qv;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<QvBean> getQv() {
                    return qv;
                }

                public void setQv(List<QvBean> qv) {
                    this.qv = qv;
                }

                public static class QvBean implements Serializable{
                    /**
                     * value : 001
                     */
                    @Override
                    public String toString() {
                        return value;
                    }

                    private String value;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }
            }
        }
    }
}

