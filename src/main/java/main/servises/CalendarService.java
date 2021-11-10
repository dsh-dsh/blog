package main.servises;

import main.dto.responses.CalendarResponse;
import main.dto.CalendarDTO;
import main.model.ModerationStatus;
import main.repositories.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public CalendarResponse getCalendar(Integer year) {

        if(year == null) {
            year = LocalDate.now().getYear();
        }
        final int finalYear = year;

        List<CalendarDTO> list = calendarRepository.getCalendar(true, ModerationStatus.ACCEPTED);

        Map<String, Long> map = list.stream()
                .collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount));
        int[] arr = map.keySet().stream()
                .map(s -> s.substring(0, 4))
                .distinct()
                .sorted()
                .mapToInt(Integer::parseInt)
                .toArray();

        map = list.stream()
                .filter(e -> e.getDate().substring(0, 4).equals(String.valueOf(finalYear)))
                .collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount));

        return new CalendarResponse(arr, map);
    }

}
