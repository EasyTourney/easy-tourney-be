package com.example.easytourneybe.user.transformer;

import com.example.easytourneybe.user.dto.OrganizerTableDto;
import org.hibernate.transform.ResultTransformer;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrganizerTransformer implements ResultTransformer {
    @Override
    public List transformList(List collection) {
        return collection;
    }
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        OrganizerTableDto organizerTableDTO = new OrganizerTableDto();
        organizerTableDTO.setId((Integer) tuple[0]);
        organizerTableDTO.setEmail((String) tuple[1]);
        organizerTableDTO.setFullName((String) tuple[2]);
        organizerTableDTO.setPhoneNumber((String) tuple[3]);
        Timestamp timestamp = (Timestamp) tuple[4];
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        organizerTableDTO.setCreatedAt(localDateTime);
        organizerTableDTO.setTotalTournament((Long) tuple[5]);
        organizerTableDTO.setDateOfBirth((LocalDate) tuple[6]);
        return organizerTableDTO;
    }


}

