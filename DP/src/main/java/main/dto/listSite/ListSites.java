package main.dto.listSite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "sites")
public class ListSites {
    private List<SiteData> list;

    @Setter
    @Getter
    public static class SiteData{
        private String url;
        private String name;
    }
}