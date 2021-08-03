package main.servises;

import main.api.responses.CalendarResponse;
import main.dto.CalendarDTO;
import main.model.ModerationStatus;
import main.repositories.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    @Autowired
    CalendarRepository calendarRepository;

    public CalendarResponse getCalendar() {
        List<CalendarDTO> list = calendarRepository.getCalendar(true, ModerationStatus.ACCEPTED);

        Map<String, Long> map = list.stream()
                .collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount));

        int[] arr = map.keySet().stream()
                .map(s -> s.substring(0, 4))
                .distinct()
                .sorted()
                .mapToInt(Integer::parseInt)
                .toArray();

        return new CalendarResponse(arr, map);
    }

}
