package com.stefanski.cars.search.rest;

import java.util.List;
import javax.validation.Valid;

import com.wordnik.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stefanski.cars.api.CarResource;
import com.stefanski.cars.search.CarFinder;
import com.stefanski.cars.store.Car;
import com.stefanski.cars.store.rest.ErrorResp;

import static com.stefanski.cars.api.Versions.API_CONTENT_TYPE;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Dariusz Stefanski
 */
// Not fully RESTful API, but convenient
@Slf4j
@RequestMapping("/cars/search")
@RestController
@Api(value = "Cars")
class SearchController {

    private CarFinder carFinder;

    @Autowired
    SearchController(CarFinder carFinder) {
        this.carFinder = carFinder;
    }

    @RequestMapping(method = POST, consumes = API_CONTENT_TYPE, produces = API_CONTENT_TYPE)
    @ApiOperation(value = "Searches cars using given filters")
    @ApiResponses(value = {
            @ApiResponse(code = HTTP_OK, message = "Success"),
            @ApiResponse(code = HTTP_BAD_REQUEST, message = "Invalid input", response = ErrorResp.class)
    })
    ResponseEntity<List<CarResource>> findCars(
            @ApiParam(value = "Filters for searching cars")
            @Valid @RequestBody CarFilters filters) {

        List<Car> cars = carFinder.find(filters);
        List<CarResource> carResources = cars.stream()
                .map(CarResource::fromCar)
                .collect(toList());
        return new ResponseEntity<>(carResources, OK);
    }
}
