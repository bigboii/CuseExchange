package com.example.sunit_lp.ideaapp;

import android.text.format.DateFormat;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Sunit-LP on 4/19/2016.
 */

public class UserAd implements Serializable {

    String Address,Category,Description,Price,SubCategory,Title,url_img,key,email,date,url_img1,url_img2,url_img3;
    //DataSnapshot dataSnapshot;     //
    //Date date;
    public UserAd() {
        //  empty default constructor, necessary for Firebase to be able to deserialize data
    }


    public UserAd(String Address,String Category,String Description,String Price, String SubCategory,String Title,String email) {
        this.Address=Address;
        this.Category=Category;
        this.Description=Description;
        this.Price=Price;
        this.SubCategory=SubCategory;
        this.Title=Title;
        this.email=email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getSubCategory() {
        return SubCategory;
    }

    public void setSubCategory(String subCategory) {
        SubCategory = subCategory;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }

   /* public DataSnapshot getDataSnapshot()
    {
        return dataSnapshot;
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot)
    {
        this.dataSnapshot = dataSnapshot;
    }*/

    /* public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date dt=new Date();
            this.date = dateFormat.format(dt).toString();
        }*/

    public String getUrl_img1() {
        return url_img1;
    }

    public void setUrl_img1(String url_img1) {
        this.url_img1 = url_img1;
    }

    public String getUrl_img2() {
        return url_img2;
    }

    public void setUrl_img2(String url_img2) {
        this.url_img2 = url_img2;
    }

    public String getUrl_img3() {
        return url_img3;
    }

    public void setUrl_img3(String url_img3) {
        this.url_img3 = url_img3;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public HashMap createAd(String Address,String Category,String Description,String Price, String SubCategory,String Title, String url_img, DataSnapshot snapshot)
   {
       HashMap ad = new HashMap();
       ad.put("address",Address);
       ad.put("category", Category);
       ad.put("description", Description);
       ad.put("price", Price);
       ad.put("subCatFilter", SubCategory);
       ad.put("title", Title);
       ad.put("url_img", url_img);

       dataSnapshot = snapshot;

       return ad;
   }*/

    static public HashMap createAd(String Address,String Category,String Description,String Price, String SubCategory,String Title, String url_img,String date)
    {
        HashMap ad = new HashMap();
        ad.put("address",Address);
        ad.put("category", Category);
        ad.put("description", Description);
        ad.put("price", Price);
        ad.put("subCatFilter", SubCategory);
        ad.put("title", Title);
        ad.put("url_img", url_img);
        ad.put("date",date);
        return ad;
    }
}
