package hz.mall.flashsale.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String tel;
    private String registerMode;
    private String thirdPartyId;
    private String encryptPassword;
}
