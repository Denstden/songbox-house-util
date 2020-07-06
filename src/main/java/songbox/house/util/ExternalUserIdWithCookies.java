package songbox.house.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserIdWithCookies {

    String userId;
    Map<String, String> cookies;

}
