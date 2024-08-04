package com.helipilatis.helipilatis.server.services;
import com.helipilatis.helipilatis.databaseModels.*;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected CalendarRepository calendarRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected InstructorRepository instructorRepository;

    @Autowired
    protected TicketTypeRepository ticketTypeRepository;

}