package org.ylabHomework.DTOs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"option"})
public class OptionDTO {
    int option;

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public OptionDTO() {
    }

    public OptionDTO(int option) {
        this.option = option;
    }
}