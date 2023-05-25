package com.kim.dto;

import com.kim.entity.Shipment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ShipmentDto {

    private Long id;

    private Long orderId;

    private Long productId;

    private LocalDateTime comDate;

    private static ModelMapper modelMapper = new ModelMapper();

    public Shipment createShipment(){
        return modelMapper.map(this, Shipment.class);
    }
    public static ShipmentDto of(Shipment shipment) {
        return modelMapper.map(shipment,ShipmentDto.class);
    }
}
