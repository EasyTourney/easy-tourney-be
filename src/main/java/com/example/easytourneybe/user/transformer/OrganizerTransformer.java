package com.example.easytourneybe.user.transformer;

import com.example.easytourneybe.user.dto.OrganizerDto;
import org.hibernate.transform.ResultTransformer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class OrganizerTransformer implements ResultTransformer {
    @Override
    public List transformList(List collection) {
        return collection;
    }
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        OrganizerDto organizerDTO = new OrganizerDto();
        organizerDTO.setId((Integer) tuple[0]);
        organizerDTO.setEmail((String) tuple[1]);
        organizerDTO.setFullName((String) tuple[2]);
        organizerDTO.setPhoneNumber((String) tuple[3]);
        Timestamp timestamp = (Timestamp) tuple[4];
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        organizerDTO.setCreatedAt(localDateTime);
        organizerDTO.setTotalTournament((Long) tuple[5]);
        return organizerDTO;
    }


}

