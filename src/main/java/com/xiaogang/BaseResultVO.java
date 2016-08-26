package com.xiaogang;

import java.io.Serializable;
import java.util.List;

public class BaseResultVO<T> implements Serializable {

	private static final long serialVersionUID = -249566887341016360L;

	private boolean success;

    private Data<T> data;

    private String msg;

    public BaseResultVO() {
        this.success = false;
    }

    public BaseResultVO(boolean success, Data<T> data, String msg) {
        this.success = success;
        this.data = data;
        this.msg = msg;
    }

    public static class Data<T> {
        private List<T> results;

        private Integer failedTotal;

        private String failedDetail;

        private Integer successTotal;

        private Integer totalPages;

        public List<T> getResults() {
            return results;
        }

        public void setResults(List<T> results) {
            this.results = results;
        }

		public Integer getFailedTotal() {
			return failedTotal;
		}

		public void setFailedTotal(Integer failedTotal) {
			this.failedTotal = failedTotal;
		}

		public String getFailedDetail() {
			return failedDetail;
		}

		public void setFailedDetail(String failedDetail) {
			this.failedDetail = failedDetail;
		}

		public Integer getSuccessTotal() {
			return successTotal;
		}

		public void setSuccessTotal(Integer successTotal) {
			this.successTotal = successTotal;
		}

		public Integer getTotalPages() {
			return totalPages;
		}

		public void setTotalPages(Integer totalPages) {
			this.totalPages = totalPages;
		}

       
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data<T> getData() {
        return data;
    }

    public void setData(Data<T> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
