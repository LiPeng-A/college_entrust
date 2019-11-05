package com.college.entrust.vo;

import com.college.entrust.pojo.Entrust;
import com.college.entrust.pojo.Image;
import lombok.Data;

import java.util.List;

@Data
public class EntrustDetail {

    private List<Entrust> entrust;

    private List<Image> images;
}
