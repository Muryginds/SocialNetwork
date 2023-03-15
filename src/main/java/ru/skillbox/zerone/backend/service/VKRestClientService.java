package ru.skillbox.zerone.backend.service;

import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.skillbox.zerone.backend.configuration.VKWebClientProperties;
import ru.skillbox.zerone.backend.exception.VKAPIException;
import ru.skillbox.zerone.backend.model.dto.response.BasicEntityDTO;
import ru.skillbox.zerone.backend.model.dto.response.VKCityResponseDTO;
import ru.skillbox.zerone.backend.model.dto.response.VKCountryResponseDTO;
import ru.skillbox.zerone.backend.model.entity.Country;
import ru.skillbox.zerone.backend.repository.CountryRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.skillbox.zerone.backend.model.dto.response.VKCityResponseDTO.VKItemsResponseDTO.CityDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class VKRestClientService {
  private final WebClient webClient;
  private final VKWebClientProperties properties;
  private final CountryRepository countryRepository;
  private static final Integer MAX_RESPONSES_NUMBER = 1000;
  private static final String ALL_COUNTRIES_CODES = """
      AF,AL,AQ,DZ,AS,AD,AO,AG,AZ,AR,AU,AT,BS,BH,BD,AM,BB,BE,BM,BT,BO,BA,BW,BV,BR,BZ,IO,SB,VG,BN,
      BG,MM,BI,BY,KH,CM,CA,CV,KY,CF,LK,TD,CL,CN,TW,CX,CC,CO,KM,YT,CG,CD,CK,CR,HR,CU,CY,CZ,BJ,DK,
      DM,DO,EC,SV,GQ,ET,ER,EE,FO,FK,GS,FJ,FI,АХ,FR,GF,PF,TF,DJ,GA,GE,GM,PS,DE,GH,GI,KI,GR,GL,GD,
      GP,GU,GT,GN,GY,HT,HM,VA,HN,HK,HU,IS,IN,ID,IR,IQ,IE,IL,IT,CI,JM,JP,KZ,JO,KE,KP,KR,KW,KG,LA,
      LB,LS,LV,LR,LY,LI,LT,LU,MO,MG,MW,MY,MV,ML,MT,MQ,MR,MU,MX,MC,MN,MD,ME,MS,MA,MZ,OM,NA,NR,NP,
      NL,CW,AW,SX,BQ,NC,VU,NZ,NI,NE,NG,NU,NF,NO,MP,UM,FM,MH,PW,PK,PA,PG,PY,PE,PH,PN,PL,PT,GW,TL,
      PR,QA,RE,RO,RU,RW,BL,SH,KN,AI,LC,MF,PM,VC,SM,ST,SA,SN,RS,SC,SL,SG,SK,VN,SI,SO,ZA,ZW,ES,EH,
      SD,SR,SJ,SZ,SE,CH,SY,TJ,TH,TG,TK,TO,TT,AE,TN,TR,TM,TC,TV,UG,UA,MK,EG,GB,GG,JE,IM,TZ,US,VI,
      BF,UY,UZ,VE,WF,WS,YE,ZM,AB,OS,SS,DN,LN
      """;

  @Scheduled(cron = "${scheduled-tasks.vk-countries-uploader}")
  public void uploadCountries() {
    var response = webClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(properties.getFindCountriesMethodUri())
            .queryParam("v", properties.getVersion())
            .queryParam("need_all", 1)
            .queryParam("count", MAX_RESPONSES_NUMBER)
            .queryParam("code", ALL_COUNTRIES_CODES)
            .queryParam("lang", "ru")
            .build())
        .retrieve()
        .bodyToMono(VKCountryResponseDTO.class)
        .doOnError(WriteTimeoutException.class, ex -> log.error("IN uploadCountries - WriteTimeoutException occurred"))
        .doOnError(ReadTimeoutException.class, ex -> log.error("IN uploadCountries - ReadTimeoutException occurred"))
        .blockOptional().orElseThrow(() -> new VKAPIException("Ошибка получения данных по API"));
    var existingCountries = countryRepository.findAll().stream()
        .map(Country::getId)
        .collect(Collectors.toSet());
    var newCountries = response.getResponse().getItems().stream()
        .filter(c -> !c.getTitle().isBlank())
        .filter(c -> !existingCountries.contains(c.getId()))
        .map(c -> new Country(c.getId(), c.getTitle()))
        .toList();
    countryRepository.saveAll(newCountries);
  }

  public List<BasicEntityDTO> getCities(int countryId, String query, int offset, int itemPerPage) {
    var response = webClient.post()
        .uri(uriBuilder -> uriBuilder
            .path(properties.getFindCitiesMethodUri())
            .queryParam("v", properties.getVersion())
            .queryParam("lang", "ru")
            .queryParam("count", itemPerPage)
            .queryParam("country_id", countryId)
            .queryParam("offset", offset)
            .queryParam("q", query)
            .build())
        .retrieve()
        .bodyToMono(VKCityResponseDTO.class)
        .doOnError(WriteTimeoutException.class, ex -> log.error("IN uploadCountries - WriteTimeoutException occurred"))
        .doOnError(ReadTimeoutException.class, ex -> log.error("IN uploadCountries - ReadTimeoutException occurred"))
        .blockOptional().orElseThrow(() -> new VKAPIException("Ошибка получения данных по API"));

    return response.getResponse().getItems().stream()
        .map(c -> new BasicEntityDTO(c.getId(), formatName(c)))
        .toList();
  }

  private String formatName(CityDTO cityDTO) {
    StringBuilder sb = new StringBuilder();
    sb.append(cityDTO.getTitle());
    if (!Objects.isNull(cityDTO.getArea())) {
      sb.append(", ").append(cityDTO.getArea());
    }
    if (!Objects.isNull(cityDTO.getRegion())) {
      sb.append(", ").append(cityDTO.getRegion());
    }
    return sb.toString();
  }
}
