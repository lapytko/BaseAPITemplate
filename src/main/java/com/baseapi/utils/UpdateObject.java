package com.baseapi.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

public class UpdateObject {

    private ModelMapper modelMapper;

    public UpdateObject() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        modelMapper.getConfiguration().setPropertyCondition(context -> context.getSource() != null && !context.getSource().equals(context.getDestination()));
    }

    public <T> T update(T source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
