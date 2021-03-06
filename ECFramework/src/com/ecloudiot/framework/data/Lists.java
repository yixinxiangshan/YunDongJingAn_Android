package com.ecloudiot.framework.data;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table LISTS.
 */
public class Lists {

    private Long id;
    private String sort1;
    private String sort2;
    private String list_type;
    private String content;
    private java.util.Date create_at;
    private String state;

    public Lists() {
    }

    public Lists(Long id) {
        this.id = id;
    }

    public Lists(Long id, String sort1, String sort2, String list_type, String content, java.util.Date create_at, String state) {
        this.id = id;
        this.sort1 = sort1;
        this.sort2 = sort2;
        this.list_type = list_type;
        this.content = content;
        this.create_at = create_at;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSort1() {
        return sort1;
    }

    public void setSort1(String sort1) {
        this.sort1 = sort1;
    }

    public String getSort2() {
        return sort2;
    }

    public void setSort2(String sort2) {
        this.sort2 = sort2;
    }

    public String getList_type() {
        return list_type;
    }

    public void setList_type(String list_type) {
        this.list_type = list_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public java.util.Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(java.util.Date create_at) {
        this.create_at = create_at;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
