package study.toy.everythingshop.entity.mariaDB;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductViews {
    private Integer productNum;         //상품번호
    private Integer views;              //조회수
    private String registerDt;          //등록일자
    private String changeDt;            //수정일자

    public void increaseView() {
        this.views += 1;
    }
}
