package hz.mall.flashsale.domain;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
