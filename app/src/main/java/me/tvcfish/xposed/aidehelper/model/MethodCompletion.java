package me.tvcfish.xposed.aidehelper.model;

import org.litepal.crud.LitePalSupport;

public class MethodCompletion extends LitePalSupport {

  private int id;
  private String english;
  private String chinese;
  private String notes;
  private int state;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEnglish() {
    return english;
  }

  public void setEnglish(String english) {
    this.english = english;
  }

  public String getChinese() {
    return chinese;
  }

  public void setChinese(String chinese) {
    this.chinese = chinese;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

}
