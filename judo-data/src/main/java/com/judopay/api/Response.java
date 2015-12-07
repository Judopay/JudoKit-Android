package com.judopay.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Response implements Parcelable {

    private String result;

    @SerializedName("errorCategory")
    private Integer errorCategory;

    @SerializedName("explanation")
    private String errorExplanation;

    @SerializedName("resolution")
    private String errorResolution;

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private String errorCode;

    @SerializedName("details")
    private List<ApiError> errorDetails;

    public boolean isSuccess() {
        return "Success".equals(result);
    }

    public boolean isDeclined() {
        return "Declined".equals(result);
    }

    public String getResult() {
        return result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Integer getErrorCategory() {
        return errorCategory;
    }

    public String getErrorExplanation() {
        return errorExplanation;
    }

    public String getErrorResolution() {
        return errorResolution;
    }

    public List<ApiError> getErrorDetails() {
        return errorDetails;
    }

    @Override
    public String toString() {
        return "Response{" +
                "errorDetails=" + errorDetails +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", errorResolution='" + errorResolution + '\'' +
                ", errorExplanation='" + errorExplanation + '\'' +
                ", errorCategory=" + errorCategory +
                ", result='" + result + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeValue(this.errorCategory);
        dest.writeString(this.errorExplanation);
        dest.writeString(this.errorResolution);
        dest.writeString(this.message);
        dest.writeString(this.errorCode);
        dest.writeList(this.errorDetails);
    }

    public Response() { }

    protected Response(Parcel in) {
        this.result = in.readString();
        this.errorCategory = (Integer) in.readValue(Integer.class.getClassLoader());
        this.errorExplanation = in.readString();
        this.errorResolution = in.readString();
        this.message = in.readString();
        this.errorCode = in.readString();
        this.errorDetails = new ArrayList<>();
        in.readList(this.errorDetails, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel source) {
            return new Response(source);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}