package wef.articulab.control.controllers;

import wef.articulab.control.controllers.BehaviorNetworkController;

/**
 * Created by oscarr on 4/28/16.
 */
public class InMindBN extends BehaviorNetworkController {
    public static final String DEFINE_DESTINATION = "DEFINE-DESTINATION";
    public static final String NO_DESTINATION = "NO-DESTINATION";
    public static final String PLAN_TRAVEL_REQUEST = "PLAN-TRAVEL-REQUEST";
    public static final String OK_DESTINATION = "OK-DESTINATION";
    public static final String TRAVEL_TO_DESTINATION = "TRAVEL-TO-DESTINATION";
    public static final String CHECK_TRIP_DATES = "CHECK-TRIP-DATES";
    public static final String NO_TRAVEL_DATES = "NO-TRAVEL-DATES";
    public static final String OK_TRAVEL_DATES = "OK-TRAVEL-DATES";
    public static final String RE_SCHEDULE_TRIP = "RE-SCHEDULE-TRIP";
    public static final String EVENT_ADDED = "EVENT-ADDED";
    public static final String CHECK_WEATHER_TRIP = "CHECK-WEATHER-TRIP";
    public static final String NO_WEATHER_CHECKED = "NO-WEATHER-CHECKED";
    public static final String WEATHER_CHECKED = "WEATHER-CHECKED";
    public static final String CHECK_NEWS = "CHECK-NEWS";
    public static final String NO_NEWS_CHECKED = "NO-NEWS-CHECKED";
    public static final String NEWS_CHECKED = "NEWS-CHECKED";
    public static final String BOOK_HOTEL = "BOOK-HOTEL";
    public static final String NO_HOTEL_BOOKED = "NO-HOTEL-BOOKED";
    public static final String HOTEL_BOOKED = "HOTEL-BOOKED";
    public static final String FLIGHT_BOOKED = "FLIGHT-BOOKED";
    public static final String NO_FLIGHT_BOOKED = "NO-FLIGHT-BOOKED";
    public static final String MAKE_ITINERARY = "MAKE-ITINERARY";
    public static final String CHECK_EVENT_DATES = "CHECK-EVENT-DATES";
    public static final String ADD_EVENT_REQUEST = "ADD-EVENT-REQUEST";
    public static final String NO_EVENT_DATE = "NO-EVENT-DATE";
    public static final String ADD_EVENT_CALENDAR = "ADD-EVENT-CALENDAR";
    public static final String OK_EVENT_DATE = "OK-EVENT-DATE";
    public static final String BOOK_FLIGHT = "BOOK-FLIGHT";
    public static final String OVERLAPPED_DATES = "OVERLAPPED-DATES";



//    @Override
//    public BehaviorNetworkInterface createBN() {
//        network = new BehaviorNetwork();
//
//        Behavior beh1 = new Behavior( DEFINE_DESTINATION,
//                new String[]{NO_DESTINATION, PLAN_TRAVEL_REQUEST},
//                new String[]{OK_DESTINATION},
//                new String[]{NO_DESTINATION});
//
//        Behavior beh2 = new Behavior( CHECK_TRIP_DATES,
//                new String[]{NO_TRAVEL_DATES, PLAN_TRAVEL_REQUEST},
//                new String[]{OK_TRAVEL_DATES},
//                new String[]{NO_TRAVEL_DATES});
//
//        Behavior beh3 = new Behavior( RE_SCHEDULE_TRIP,
//                new String[]{OVERLAPPED_DATES},
//                new String[]{NO_TRAVEL_DATES, PLAN_TRAVEL_REQUEST},
//                new String[]{OVERLAPPED_DATES});
//
//        Behavior beh4 = new Behavior( CHECK_WEATHER_TRIP,
//                new String[]{OK_TRAVEL_DATES, PLAN_TRAVEL_REQUEST, NO_WEATHER_CHECKED, OK_DESTINATION},
//                new String[]{WEATHER_CHECKED},
//                new String[]{NO_WEATHER_CHECKED});
//
//        Behavior beh5 = new Behavior( CHECK_NEWS,
//                new String[]{OK_TRAVEL_DATES, PLAN_TRAVEL_REQUEST, NO_NEWS_CHECKED, OK_DESTINATION},
//                new String[]{NEWS_CHECKED},
//                new String[]{NO_NEWS_CHECKED});
//
//        Behavior beh6 = new Behavior( BOOK_HOTEL,
//                new String[]{NEWS_CHECKED, WEATHER_CHECKED, PLAN_TRAVEL_REQUEST, NO_HOTEL_BOOKED, OK_DESTINATION, OK_TRAVEL_DATES},
//                new String[]{HOTEL_BOOKED},
//                new String[]{NO_HOTEL_BOOKED});
//
//        Behavior beh7 = new Behavior( BOOK_FLIGHT,
//                new String[]{NEWS_CHECKED, WEATHER_CHECKED, PLAN_TRAVEL_REQUEST, NO_FLIGHT_BOOKED, OK_DESTINATION, OK_TRAVEL_DATES},
//                new String[]{FLIGHT_BOOKED},
//                new String[]{NO_FLIGHT_BOOKED});
//
//        Behavior beh8 = new Behavior( MAKE_ITINERARY,
//                new String[]{PLAN_TRAVEL_REQUEST, HOTEL_BOOKED, FLIGHT_BOOKED, OK_DESTINATION},
//                new String[]{TRAVEL_TO_DESTINATION},
//                new String[]{PLAN_TRAVEL_REQUEST, OK_TRAVEL_DATES, HOTEL_BOOKED, FLIGHT_BOOKED, OK_DESTINATION, WEATHER_CHECKED, NEWS_CHECKED});
//
//        Behavior beh9 = new Behavior( CHECK_EVENT_DATES,
//                new String[]{ADD_EVENT_REQUEST, NO_EVENT_DATE},
//                new String[]{ADD_EVENT_CALENDAR, OK_EVENT_DATE},
//                new String[]{NO_EVENT_DATE});
//
//        Behavior beh10 = new Behavior( ADD_EVENT_CALENDAR,
//                new String[]{OK_EVENT_DATE, ADD_EVENT_REQUEST, ADD_EVENT_CALENDAR},
//                new String[]{EVENT_ADDED},
//                new String[]{OK_EVENT_DATE, ADD_EVENT_REQUEST, ADD_EVENT_CALENDAR});
//
//        states.add( NO_DESTINATION );
//        states.add(NO_TRAVEL_DATES);
//        states.add( NO_WEATHER_CHECKED );
//        states.add( NO_NEWS_CHECKED );
//        states.add( NO_HOTEL_BOOKED );
//        states.add( NO_FLIGHT_BOOKED );
//        states.add( NO_EVENT_DATE );
//
//        modules.add(beh1);
//        modules.add(beh2);
//        modules.add(beh3);
//        modules.add(beh4);
//        modules.add(beh5);
//        modules.add(beh6);
//        modules.add(beh7);
//        modules.add(beh8);
//        modules.add(beh9);
//        modules.add(beh10);
//
//        NUM_BEHAVIORS = modules.size();
//        NUM_VARIABLES = NUM_BEHAVIORS + 2;
//
//        network.setGoals(goals);
//        network.setPi(20);
//        network.setTheta(15);
//        network.setInitialTheta(15);
//        network.setPhi(70);
//        network.setGamma(40);
//        network.setDelta(20);
//
//        network.setModules(modules, NUM_VARIABLES);
//        network.setState(states);
//        network.setGoalsR(new Vector<>());
//        //use this only if you need the goal to be executed one time
//        //network1.setOneTimeGoals( Arrays.asList( new String[]{ EVENT_ADDED, TRAVEL_TO_DESTINATION } ) );
//        return network;
//    }

    @Override
    public String extractState(String state) {
        return null;
    }

    public String removeState( String state ){
        return null;
    }
}
