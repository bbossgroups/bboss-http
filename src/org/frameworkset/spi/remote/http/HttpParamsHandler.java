package org.frameworkset.spi.remote.http;
/**
 * Copyright 2023 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.frameworkset.spi.remote.http.proxy.ExceptionWare;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;
import org.frameworkset.spi.remote.http.proxy.HttpServiceHosts;
import org.frameworkset.util.ClassUtil;
import org.frameworkset.util.annotations.DateFormateMeta;
import org.frameworkset.util.annotations.wraper.RequestParamWraper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2023</p>
 * @Date 2023/7/28
 * @author biaoping.yin
 * @version 1.0
 */
public class HttpParamsHandler {
    public static final String defaultDateformat = "yyyy-MM-dd HH:mm:ss";

    public static Exception getException(ResponseHandler responseHandler, HttpServiceHosts httpServiceHosts ){
//        assertCheck(  httpServiceHosts );
        ExceptionWare exceptionWare = httpServiceHosts.getExceptionWare();
        if(exceptionWare != null) {
            return exceptionWare.getExceptionFromResponse(responseHandler);
        }
        return null;
    }

    /**
     * 拼接get请求参数
     * @param url
     * @param params
     * @return
     */
    public static String appendParams(String url,Object params){
        if(params == null){
            return url;
        }
        else if(params instanceof Map){
            return appendMapParams(url,(Map)params);
        }
        else{
            return appendObjectParams(url,params);
        }

    }

    /**
     * 拼接get请求参数
     * @param url
     * @param params
     * @return
     */
    public static String appendMapParams(String url,Map params){
        int idx = url.indexOf("?");
        boolean hasParams = idx > 0;
        boolean lastIdx = idx == url.length() - 1;
        StringBuilder ret = new StringBuilder();
        ret.append(url);
        //拼接get请求参数
        if(params != null && params.size() > 0){
            Iterator<Map.Entry> iterator = params.entrySet().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String name = String.valueOf(entry.getKey());
                Object value_ = entry.getValue();
                if (value_ == null)
                    continue;
                String value = convertValue( value_, null);
                try {
                    value = java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new HttpProxyRequestException(e);
                }
                if (hasParams) {
                    if (lastIdx) {
                        ret.append(name).append("=").append(value);
                        lastIdx = false;
                    } else {
                        ret.append("&").append(name).append("=").append(value);
                    }
                } else {
                    ret.append("?").append(name).append("=").append(value);
                    hasParams = true;
                }
            }
        }
        return ret.toString();
        /**
         int idx = url.indexOf("?");
         StringBuilder ret = new StringBuilder();
         ret.append(url);
         if(idx < 0){
         ret.append("?");
         Iterator<Map.Entry> iterator = params.entrySet().iterator();
         int i = 0;
         while (iterator.hasNext()){
         Map.Entry entry = iterator.next();
         if(i > 0)
         ret.append("&");
         ret.append(entry.getKey()).append("=").append(entry.getValue());
         i ++;
         }
         }
         else{
         if(idx == url.length() - 1){
         Iterator<Map.Entry> iterator = params.entrySet().iterator();
         int i = 0;
         while (iterator.hasNext()){
         Map.Entry entry = iterator.next();
         if(i > 0)
         ret.append("&");
         ret.append(entry.getKey()).append("=").append(entry.getValue());
         i ++;
         }
         }
         else{
         Iterator<Map.Entry> iterator = params.entrySet().iterator();
         while (iterator.hasNext()){
         Map.Entry entry = iterator.next();
         ret.append("&");
         ret.append(entry.getKey()).append("=").append(entry.getValue());
         }
         }
         }
         url = ret.toString();

         }
         return url;
         */

    }

    public static String convertValue(Object value, ClassUtil.PropertieDescription propertieDescription, DataSerialType dataSerialType){
        if( value instanceof String) {
            return String.valueOf(value);
        }
        else if(value instanceof Date){
            String dateFormat = getDateformat(propertieDescription);
            return DateFormateMeta.format((Date)value,dateFormat);
        }
        else if(value instanceof LocalDateTime){
            String dateFormat = getDateformat(propertieDescription);
            return TimeUtil.changeLocalDateTime2String((LocalDateTime)value , dateFormat);
        }
        else if(value instanceof LocalDate){
            String dateFormat = getDateformat(propertieDescription);
            return TimeUtil.changeLocalDate2String((LocalDate)value , dateFormat);
        }

        else if(dataSerialType == null || dataSerialType != DataSerialType.JSON) {
            return String.valueOf(value);
        }
        else{
            return SimpleStringUtil.object2json(value);
        }
    }


    public static String convertValue(Object value, DataSerialType dataSerialType){
        if( value instanceof String) {
            return String.valueOf(value);
        }
        else if(value instanceof Date){
            return DateFormateMeta.format((Date)value,defaultDateformat);
        }
        else if(value instanceof LocalDateTime){
            return TimeUtil.changeLocalDateTime2String((LocalDateTime)value , defaultDateformat);
        }
        else if(value instanceof LocalDate){
            return TimeUtil.changeLocalDate2String((LocalDate)value , defaultDateformat);
        }

        else if(dataSerialType == null || dataSerialType != DataSerialType.JSON) {
            return String.valueOf(value);
        }
        else{
            return SimpleStringUtil.object2json(value);
        }
    }
    /**
     * 拼接get请求参数
     * @param url
     * @param params
     * @return
     */
    public static String appendObjectParams(String url,Object params)  {
        ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(params.getClass());
        List<ClassUtil.PropertieDescription> propertieDescriptions = classInfo.getPropertyDescriptors();
        int idx = url.indexOf("?");
        boolean hasParams = idx > 0;
        boolean lastIdx = idx == url.length() - 1;
        StringBuilder ret = new StringBuilder();
        ret.append(url);
        for(ClassUtil.PropertieDescription propertieDescription: propertieDescriptions) {
            String name = propertieDescription.getName();
            Object value_ = null;
            try {
                value_ = propertieDescription.getValue(params);
            } catch (IllegalAccessException e) {
                throw new HttpProxyRequestException(e);
            } catch (InvocationTargetException e) {
                throw new HttpProxyRequestException(e);
            }
            if(value_ == null)
                continue;
            String value = convertValue( value_,propertieDescription,null);
            try {
                value = java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new HttpProxyRequestException(e);
            }
            if(hasParams){
                if(lastIdx){
                    ret.append(name).append("=").append(value);
                    lastIdx = false;
                }
                else{
                    ret.append("&").append(name).append("=").append(value);
                }
            }
            else{
                ret.append("?").append(name).append("=").append(value);
                hasParams = true;
            }


        }
        return ret.toString();

    }

    public static boolean paramsHandle(MultipartEntityBuilder multipartEntityBuilder, HttpRequestProxy.HttpOption httpOption) throws HttpProxyRequestException {
        Object params = httpOption.params;
        if (params != null) {
            if(params instanceof Map){
                return mapParamsHandle( multipartEntityBuilder, httpOption);
            }
            else{
                try {
                    return objectParamsHandle( multipartEntityBuilder, httpOption);
                } catch (InvocationTargetException e) {
                    throw new HttpProxyRequestException(e);
                } catch (IllegalAccessException e) {
                    throw new HttpProxyRequestException(e);
                }
            }
        }
        else{
            return false;
        }
    }


    private static String getDateformat(ClassUtil.PropertieDescription propertieDescription){
        RequestParamWraper requestParam = propertieDescription.getRequestParam();
        String dateFormat = null;
        if(requestParam != null && requestParam.dateformat() != null){
            dateFormat = requestParam.dateformat();
        }
        else{
            dateFormat = defaultDateformat;
        }
        return dateFormat;
    }

    private static boolean objectParamsHandle(MultipartEntityBuilder multipartEntityBuilder, HttpRequestProxy.HttpOption httpOption) throws InvocationTargetException, IllegalAccessException {
        boolean hasdata = false;

        if (httpOption.params != null) {
            Object params = (Object)httpOption.params;
            ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(params.getClass());
            List<ClassUtil.PropertieDescription> propertieDescriptions = classInfo.getPropertyDescriptors();
            for(ClassUtil.PropertieDescription propertieDescription: propertieDescriptions) {
                String name = propertieDescription.getName();
                Object value_ = propertieDescription.getValue(params);

                if(value_ == null)
                    continue;
                String value = convertValue( value_,propertieDescription,httpOption.dataSerialType);
                multipartEntityBuilder.addTextBody(name, value, ClientConfiguration.TEXT_PLAIN_UTF_8);
//                if( value instanceof String) {
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(value instanceof Date){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = DateFormateMeta.format((Date)value,dateFormat);
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(value instanceof LocalDateTime){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = TimeUtil.changeLocalDateTime2String((LocalDateTime)value , dateFormat);
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(value instanceof LocalDate){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = TimeUtil.changeLocalDate2String((LocalDate)value , dateFormat);
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(httpOption.dataSerialType != DataSerialType.JSON || value instanceof String) {
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else{
//
//                    multipartEntityBuilder.addTextBody(name, SimpleStringUtil.object2json(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
                hasdata = true;
            }

        }
        return hasdata;
    }

    public static List<NameValuePair> paramsPaires(HttpRequestProxy.HttpOption httpOption) throws HttpProxyRequestException {
        Object params = httpOption.params;
        List<NameValuePair> pairs = null;
        if (params != null) {
            if(params instanceof Map){
                pairs =  mapParamsPairs(  httpOption);
            }
            else{
                try {
                    pairs =  objectParamsPairs(  httpOption);
                } catch (InvocationTargetException e) {
                    throw new HttpProxyRequestException(e);
                } catch (IllegalAccessException e) {
                    throw new HttpProxyRequestException(e);
                }
            }
        }
//        if(pairs == null){
//            pairs = new ArrayList<>(0);
//        }
        return pairs;
    }

    private static List<NameValuePair> objectParamsPairs(HttpRequestProxy.HttpOption httpOption) throws InvocationTargetException, IllegalAccessException {
        List<NameValuePair> paramPair = new ArrayList<NameValuePair>();

        if (httpOption.params != null) {
            Object params = (Object)httpOption.params;
            ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(params.getClass());
            List<ClassUtil.PropertieDescription> propertieDescriptions = classInfo.getPropertyDescriptors();
            NameValuePair paramPair_ = null;
            for(ClassUtil.PropertieDescription propertieDescription: propertieDescriptions) {
                String name = propertieDescription.getName();
                Object value_ = propertieDescription.getValue(params);
                if(value_ == null)
                    continue;
                String value = convertValue(value_,propertieDescription,httpOption.dataSerialType);
                paramPair_ = new BasicNameValuePair(name, value);
//                if( value instanceof String) {
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(value instanceof Date){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = DateFormateMeta.format((Date)value,dateFormat);
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(value instanceof LocalDateTime){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = TimeUtil.changeLocalDateTime2String((LocalDateTime)value , dateFormat);
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(value instanceof LocalDate){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = TimeUtil.changeLocalDate2String((LocalDate)value , dateFormat);
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(httpOption.dataSerialType != DataSerialType.JSON) {
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else{
//                    paramPair_ = new BasicNameValuePair(name, SimpleStringUtil.object2json(value));
//                }
                paramPair.add(paramPair_);
            }

        }
        return paramPair;


    }

    private static List<NameValuePair> mapParamsPairs(HttpRequestProxy.HttpOption httpOption){
        Map params = (Map)httpOption.params;
        if(params.size() <= 0)
            return null;
        List<NameValuePair> paramPair = new ArrayList<NameValuePair>();

        Iterator<Map.Entry> it = params.entrySet().iterator();
        NameValuePair paramPair_ = null;
        for (int i = 0; it.hasNext(); i++) {
            Map.Entry entry = it.next();
            Object value_ = entry.getValue();

            if(value_ == null)
                continue;
            String value = convertValue(value_,httpOption.dataSerialType);
            String name = String.valueOf(entry.getKey());
            paramPair_ = new BasicNameValuePair(name, value);
//            if(value instanceof String) {
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(value instanceof Date){
//                value = DateFormateMeta.format((Date)value,defaultDateformat);
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(value instanceof LocalDateTime){
//                value = TimeUtil.changeLocalDateTime2String((LocalDateTime)value , defaultDateformat);
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(value instanceof LocalDate){
//                value = TimeUtil.changeLocalDate2String((LocalDate)value , defaultDateformat);
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(httpOption.dataSerialType != DataSerialType.JSON ) {
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else{
//                paramPair_ = new BasicNameValuePair(name, SimpleStringUtil.object2json(value));
//            }
            paramPair.add(paramPair_);
        }
        return paramPair;
    }


    private static HttpParams objectHttpParams(Object params) {
//        if (params != null && params.size() > 0) {
//            httpParams = new BasicHttpParams();
//            Iterator<Entry> it = params.entrySet().iterator();
//            for (int i = 0; it.hasNext(); i++) {
//                Entry entry = it.next();
//                httpParams.setParameter(String.valueOf(entry.getKey()), entry.getValue());
//            }
//        }
        HttpParams httpParams = null;
        if (params != null) {
            httpParams = new BasicHttpParams();
            ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(params.getClass());
            List<ClassUtil.PropertieDescription> propertieDescriptions = classInfo.getPropertyDescriptors();
            for(ClassUtil.PropertieDescription propertieDescription: propertieDescriptions) {
                String name = propertieDescription.getName();
                Object value_ = null;
                try {
                    value_ = propertieDescription.getValue(params);
                } catch (IllegalAccessException e) {
                    throw new HttpProxyRequestException(e);
                } catch (InvocationTargetException e) {
                    throw new HttpProxyRequestException(e);
                }
                if(value_ == null)
                    continue;
                String value = convertValue(value_,propertieDescription,null);
                httpParams.setParameter(name, value);
//                if( value instanceof String) {
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(value instanceof Date){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = DateFormateMeta.format((Date)value,dateFormat);
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(value instanceof LocalDateTime){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = TimeUtil.changeLocalDateTime2String((LocalDateTime)value , dateFormat);
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(value instanceof LocalDate){
//                    String dateFormat = getDateformat(propertieDescription);
//                    value = TimeUtil.changeLocalDate2String((LocalDate)value , dateFormat);
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else if(httpOption.dataSerialType != DataSerialType.JSON) {
//                    paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//                }
//                else{
//                    paramPair_ = new BasicNameValuePair(name, SimpleStringUtil.object2json(value));
//                }
            }

        }
        return httpParams;


    }

    public static HttpParams httpParams(Object params){
        if(params == null){
            return null;
        }
        if(params instanceof Map){
            return mapHttpParams((Map)params);
        }
        else{
            return objectHttpParams(params);
        }

    }

    private static HttpParams mapHttpParams(Map params){
        if(params.size() <= 0)
            return null;
        HttpParams httpParams = new BasicHttpParams();

        Iterator<Map.Entry> it = params.entrySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            Map.Entry entry = it.next();
            Object value_ = entry.getValue();

            if(value_ == null)
                continue;
            String value = convertValue(value_,null);
            String name = String.valueOf(entry.getKey());
            httpParams.setParameter(name, value);
//            if(value instanceof String) {
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(value instanceof Date){
//                value = DateFormateMeta.format((Date)value,defaultDateformat);
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(value instanceof LocalDateTime){
//                value = TimeUtil.changeLocalDateTime2String((LocalDateTime)value , defaultDateformat);
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(value instanceof LocalDate){
//                value = TimeUtil.changeLocalDate2String((LocalDate)value , defaultDateformat);
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else if(httpOption.dataSerialType != DataSerialType.JSON ) {
//                paramPair_ = new BasicNameValuePair(name, String.valueOf(value));
//            }
//            else{
//                paramPair_ = new BasicNameValuePair(name, SimpleStringUtil.object2json(value));
//            }
        }
        return httpParams;
    }

    private static boolean mapParamsHandle(MultipartEntityBuilder multipartEntityBuilder, HttpRequestProxy.HttpOption httpOption){
        boolean hasdata = false;
        Map params = (Map)httpOption.params;
        if (params != null) {
            Iterator<Map.Entry> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                Object value_ = entry.getValue();
                if(value_ == null)
                    continue;
                String value = convertValue(value_,httpOption.dataSerialType);
                String name = String.valueOf(entry.getKey());
                multipartEntityBuilder.addTextBody(name, value, ClientConfiguration.TEXT_PLAIN_UTF_8);
//                if(value instanceof String) {
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(value instanceof Date){
//                    value = DateFormateMeta.format((Date)value,defaultDateformat);
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(value instanceof LocalDateTime){
//                    value = TimeUtil.changeLocalDateTime2String((LocalDateTime)value , defaultDateformat);
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(value instanceof LocalDate){
//                    value = TimeUtil.changeLocalDate2String((LocalDate)value , defaultDateformat);
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else if(httpOption.dataSerialType != DataSerialType.JSON) {
//                    multipartEntityBuilder.addTextBody(name, String.valueOf(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
//                else{
//
//                    multipartEntityBuilder.addTextBody(name, SimpleStringUtil.object2json(value), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                }
                hasdata = true;
            }
        }
        return hasdata;
    }
}
