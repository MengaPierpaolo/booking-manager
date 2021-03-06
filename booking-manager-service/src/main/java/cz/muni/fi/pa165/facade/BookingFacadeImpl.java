package cz.muni.fi.pa165.facade;

import cz.muni.fi.pa165.dto.BookingDto;
import cz.muni.fi.pa165.dto.CreateBookingDto;
import cz.muni.fi.pa165.entity.Booking;
import cz.muni.fi.pa165.entity.Customer;
import cz.muni.fi.pa165.entity.Hotel;
import cz.muni.fi.pa165.entity.Room;
import cz.muni.fi.pa165.service.BookingService;
import cz.muni.fi.pa165.service.CustomerService;
import cz.muni.fi.pa165.service.HotelService;
import cz.muni.fi.pa165.service.RoomService;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BookingFacade} interface
 *
 * @author Ivo Hradek
 * @see BookingFacade
 */
@Service
@Transactional
public class BookingFacadeImpl implements BookingFacade {

    @Autowired
    BookingService bookingService;

    @Autowired
    RoomService roomService;
    @Autowired
    CustomerService customerService;

    @Autowired
    HotelService hotelService;

    @Autowired
    Mapper mapper;

    @Override
    public BookingDto getBookingById(Long id) {
        return mapper.map(bookingService.getBookingById(id), BookingDto.class);
    }

    @Override
    public Collection<BookingDto> getAllRoomBookings(Long id) {
        Room room = roomService.getRoomDtoById(id);
        return (Collection) ((List<Booking>) bookingService.getAllBookings()).stream()
                .filter(b -> b.getRoom().getId() == room.getId())
                .map(b -> mapper.map(b, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllHotelBookings(Long id) {
        Hotel hotel = hotelService.getHotelById(id);
        return (Collection) ((List<Booking>) bookingService.getAllBookings()).stream()
                .filter(b -> b.getRoom().getHotel().getId() == hotel.getId())
                .map(b -> mapper.map(b, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllBookings() {
        return (Collection) ((List) bookingService.getAllBookings()).stream()
                .map(b -> mapper.map(b, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(Long id) {
        bookingService.deleteBooking(bookingService.getBookingById(id));
    }

    @Override
    public Long createBooking(CreateBookingDto bookingDto) {
        Booking booking = mapper.map(bookingDto, Booking.class);
        Customer customer = customerService.getCustomerById(bookingDto.getCustomerId());
        Room room = roomService.getRoomDtoById(bookingDto.getRoomId());

        //convert to yoda time
        DateTime date1 = bookingDto.getCheckIn()==null? null:new DateTime(bookingDto.getCheckIn());
        DateTime date2 = bookingDto.getCheckOut()==null? null:new DateTime(bookingDto.getCheckOut());

        //calculate final price of booking
        BigDecimal price;
        if(date1 == null || date2 == null){
             price = BigDecimal.ZERO;
        }
        else {
             price = room.getPrice().multiply(
                    new BigDecimal(String.valueOf(Days.daysBetween(date1, date2).getDays())));

        }
        booking.setPrice(price);
        booking.setCustomer(customer);
        booking.setRoom(room);
        Booking newBooking = bookingService.addBooking(booking);
        return newBooking.getId();
    }
}
