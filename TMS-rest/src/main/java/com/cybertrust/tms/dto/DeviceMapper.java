package com.cybertrust.tms.dto;
 
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import com.cybertrust.tms.config.DemoAppConfig;
import com.cybertrust.tms.entity.Device;
import com.cybertrust.tms.entity.TrustedUser;


@Mapper(componentModel="spring", uses = ReferenceMapper.class)
public interface DeviceMapper {

	DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);

	DeviceDto deviceToDeviceDto(Device device);
	Device deviceDtoToDevice(DeviceDto device);
	List<DeviceDto> toDeviceDtos(List<Device> d);
	
	TrustedUserDto trustedUserToTrustedUserDto(TrustedUser u);
}
