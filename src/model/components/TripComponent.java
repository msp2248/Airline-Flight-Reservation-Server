/**
 * TripCompenent interface
 * @author Meet Patel
 */

package model.components;

import java.text.ParseException;
import java.util.Date;

public interface TripComponent {

    int getTime() throws ParseException;
    int getAirfare();
    Airport getOrigin();
    Airport getDestination();
    Date getArrival();
    Date getDeparture();
    int getId();
}
