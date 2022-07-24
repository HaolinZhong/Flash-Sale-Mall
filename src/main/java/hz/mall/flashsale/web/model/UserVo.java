package hz.mall.flashsale.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String tel;
}
