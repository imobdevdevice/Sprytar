package com.novp.sprytar.data;

import android.net.Uri;
import android.support.v4.util.ArrayMap;

import com.novp.sprytar.R;
import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.Faq;
import com.novp.sprytar.data.model.GameRule;
import com.novp.sprytar.data.model.Location;
import com.novp.sprytar.data.model.LocationBoundary;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.domain.DataRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DummyRepository implements DataRepository {

    public static final List<Location> locations = new ArrayList<Location>();

    public static final List<LocationBoundary> shapeMarkers = new ArrayList<>();

    public static final List<Amenity> locationMarkers = new ArrayList<>();

    public static final List<VenueActivity> venueActivities = new ArrayList<>();

    public static final List<Faq> faqList = new ArrayList<>();

    public static final ArrayMap<String, Uri> avatarMap = new ArrayMap<>();

    public static final List<GameRule> rules = new ArrayList<>();

    public static final List<VenueActivity> hardcodedActivities = new ArrayList<>();

    static {

        VenueActivity fitnessClass = new VenueActivity.Builder()
                .setId(101)
                .setName("Fitness classes")
                .setType(VenueActivity.FITNESS)
                .build();

        VenueActivity guidedTour = new VenueActivity.Builder()
                .setId(102)
                .setName("Guided tours")
                .setType(VenueActivity.GUIDED_TOURS)
                .build();

        hardcodedActivities.add(fitnessClass);
        hardcodedActivities.add(guidedTour);

        avatarMap.put("avatar1", Uri.parse("res:///" + R.drawable.avatar1));
        avatarMap.put("avatar2", Uri.parse("res:///" + R.drawable.avatar2));
        avatarMap.put("avatar3", Uri.parse("res:///" + R.drawable.avatar3));
        avatarMap.put("avatar4", Uri.parse("res:///" + R.drawable.avatar4));
        avatarMap.put("avatar5", Uri.parse("res:///" + R.drawable.avatar5));
        avatarMap.put("avatar6", Uri.parse("res:///" + R.drawable.avatar6));
        avatarMap.put("avatar7", Uri.parse("res:///" + R.drawable.avatar7));
        avatarMap.put("avatar8", Uri.parse("res:///" + R.drawable.avatar8));
        avatarMap.put("avatar9", Uri.parse("res:///" + R.drawable.avatar9));

//        VenueActivityDetail distanceActive = new VenueActivityDetail(0, true);
//        VenueActivityDetail distanceInactive = new VenueActivityDetail(0, false);
//
//        VenueActivityDetail gameActive = new VenueActivityDetail(1, true);
//        VenueActivityDetail gameInactive = new VenueActivityDetail(1, false);
//
//        VenueActivityDetail houseActive = new VenueActivityDetail(2, true);
//        VenueActivityDetail houseInactive = new VenueActivityDetail(2, false);
//
//        VenueActivityDetail leafActive = new VenueActivityDetail(3, true);
//        VenueActivityDetail leafInactive = new VenueActivityDetail(3, false);
//
//        VenueActivityDetail fruitActive = new VenueActivityDetail(4, true);
//        VenueActivityDetail fruitInactive = new VenueActivityDetail(4, false);
//
//        List<VenueActivityDetail> treasureDetails = new ArrayList<>();
//        treasureDetails.add(distanceActive);
//        treasureDetails.add(gameActive);
//        treasureDetails.add(houseActive);
//        treasureDetails.add(fruitInactive);
//        treasureDetails.add(leafInactive);
//
//        List<VenueActivityDetail> physicalDetails = new ArrayList<>();
//        physicalDetails.add(distanceActive);
//        physicalDetails.add(leafActive);
//        physicalDetails.add(fruitActive);
//
//        List<VenueActivityDetail> cluesDetails = new ArrayList<>();
//        cluesDetails.add(distanceInactive);
//        cluesDetails.add(gameInactive);
//        cluesDetails.add(houseInactive);
//        cluesDetails.add(fruitInactive);
//
//        List<VenueActivityDetail> defuseDetails = new ArrayList<>();
//        defuseDetails.add(distanceInactive);
//        defuseDetails.add(gameInactive);
//        defuseDetails.add(houseInactive);
//        defuseDetails.add(fruitInactive);
//
//        List<VenueActivityDetail> viewpointDetails = new ArrayList<>();
//        viewpointDetails.add(distanceInactive);
//        viewpointDetails.add(gameInactive);
//        viewpointDetails.add(leafActive);
//        viewpointDetails.add(fruitActive);
//
//        rules.add(new GameRule(0, "Be Careful!", "Remember to use your common sense when
// playing the Sprytar app.  Be aware of any local, seasonal or other hazards that might be in
// your path."));
//        rules.add(new GameRule(1, "Have Fun", "The Sprytar Treasure Hunt activity allows you to
// find fun facts and learn about the sometimes hidden cultural treasures of your local park."));
//        rules.add(new GameRule(2, "Learn Lots", "The Sprytar Treasure Hunt activity is an
// educational journey cram-packed with information about your park and local area."));
//
//        //<!--    Burnby Hall Gardens    -->
//
//        List<Amenity> burnbyMarkers = new ArrayList<>();
//        burnbyMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.730756, -1.8397087));
//
//        List<LocationBoundary> burnbyShape = new ArrayList<>();
////        burnbyShape.add(new LocationBoundary(50.496503, 30.511092));
////        burnbyShape.add(new LocationBoundary(50.496522, 30.512576));
////        burnbyShape.add(new LocationBoundary(50.496260, 30.512643));
////        burnbyShape.add(new LocationBoundary(50.496280, 30.511096));
//
//        burnbyShape.add(new LocationBoundary(53.927379, -0.776225));
//        burnbyShape.add(new LocationBoundary(53.928352, -0.777065));
//        burnbyShape.add(new LocationBoundary(53.928747, -0.776265));
//        burnbyShape.add(new LocationBoundary(53.928548, -0.774469));
//        burnbyShape.add(new LocationBoundary(53.929160, -0.772538));
//        burnbyShape.add(new LocationBoundary(53.924299, -0.768063));
//        burnbyShape.add(new LocationBoundary(53.927227, -0.776096));
//
//        List<VenueActivity> burnbyVenueActivities = new ArrayList<>();
//
////        VenueActivity burnbyHunt = new VenueActivity("Treasure Hunt", 0, 50.496503, 30.511092,
////                treasureDetails);
//
//        VenueActivity burnbyHunt = new VenueActivity("Treasure Hunt", 0, 53.9273715,-0.776958,
//                treasureDetails);
//
//        burnbyHunt.setDescription("Just like traditional treasure hunts the goal is to answer
// the question to win treasure, or in this case points. Merryn will ask the questions and you
// have to find the answers.");
//        burnbyHunt.setGamePoints(1800);
//        burnbyHunt.setRules(rules);
//        burnbyHunt.setJsonQestionsFilename("questions/burnby.json");
////        burnbyHunt.setJsonQestionsFilename("questions/vadim.json");
//
//        burnbyVenueActivities.add(burnbyHunt);
//
//        Location burnbyLocation = new Location(0, "Burnby Hall Gardens",1, 53.9273715,
// -0.776958, burnbyVenueActivities);
//        burnbyLocation.setBoundaries(burnbyShape);
//        burnbyLocation.setAmenities(burnbyMarkers);
//
//        List<Answer> answers1 = new ArrayList<>();
//        answers1.add(new Answer(0, "1508", false));
//        answers1.add(new Answer(1, "1812", false));
//        answers1.add(new Answer(2, "1745", true));
//        answers1.add(new Answer(3, "1950", false));
//
//        List<Question> questions = new ArrayList<>();
//
//        questions.add(new Question(0, "What year was the house built?", answers1, "The date
// stone above the main (south) entrance"));
//
//        List<Answer> answers2 = new ArrayList<>();
//        answers2.add(new Answer(0, "William the Conqueror", true));
//        answers2.add(new Answer(1, "Henry VII", false));
//        answers2.add(new Answer(2, "Elizabeth I", false));
//        answers2.add(new Answer(3, "Charles II", false));
//
//        questions.add(new Question(1, "Which English King granted the lands on which the park " +
//                "stands to Ilbert De Lacy?", answers2, "It was in appreciation of his military
// help during the Harrying of the North"));
//
//        List<Answer> answers3 = new ArrayList<>();
//        answers3.add(new Answer(0, "A type of farmers bale", false));
//        answers3.add(new Answer(1, "A medieval enclosure", true));
//        answers3.add(new Answer(2, "A circular meadow", false));
//        answers3.add(new Answer(3, "Something ‘nearby’", false));
//
//        questions.add(new Question(2, "What does the name RoundHay mean?", answers3, "It
// usually refers to an enclosure for Deer Hunting"));
//
//        List<Answer> answers4 = new ArrayList<>();
//        answers4.add(new Answer(0, "Quarries and mines", true));
//        answers4.add(new Answer(1, "Early housing stock", false));
//        answers4.add(new Answer(2, "Glaciers", false));
//        answers4.add(new Answer(3, "Extensive rabbit warrens", false));
//
//        questions.add(new Question(3, "What were the lakes before they were lakes?", answers4,
// "Below the lower lake is the remains of a coal mine"));
//
//        List<Answer> answers5 = new ArrayList<>();
//        answers5.add(new Answer(0, "1497", false));
//        answers5.add(new Answer(1, "1750", false));
//        answers5.add(new Answer(2, "1915", false));
//        answers5.add(new Answer(3, "1811", true));
//
//        questions.add(new Question(4, "When was the mansion started?", answers5,
//                "It took 15 years eventually finishing in 1826"));
//
//        List<Answer> answers6 = new ArrayList<>();
//        answers6.add(new Answer(0, "Thomas Telford", false));
//        answers6.add(new Answer(1, "Sir Christopher Wren", false));
//        answers6.add(new Answer(2, "Thomas Nicholson", true));
//        answers6.add(new Answer(3, "William the Conqueror", false));
//
//        questions.add(new Question(5, "Who built the castle folly in the park?", answers6,
//                "It was destroyed / can still be seen to this day"));
//
//        List<Answer> answers7 = new ArrayList<>();
//        answers7.add(new Answer(0, "1871", true));
//        answers7.add(new Answer(1, "1720", false));
//        answers7.add(new Answer(2, "1930", false));
//        answers7.add(new Answer(3, "1955", false));
//
//        questions.add(new Question(6, "When did Leeds City Council buy the land and make it a " +
//                "park?", answers7,
//                "They had to get an act of parliament to make the purchase"));
//
//        List<Answer> answers8 = new ArrayList<>();
//        answers8.add(new Answer(0, "Queen Victoria", false));
//        answers8.add(new Answer(1, "Prince Arthur", true));
//        answers8.add(new Answer(2, "The Prince Regent", false));
//        answers8.add(new Answer(3, "Thomas Cromwell", false));
//
//        questions.add(new Question(7, "Who reopened the park in 1872?", answers8,
//                "A crowd of 100,000 people gathered to see the opening"));
//
//        List<Answer> answers9 = new ArrayList<>();
//        answers9.add(new Answer(0, "Carp", false));
//        answers9.add(new Answer(1, "A dedicated rabbit keeper", false));
//        answers9.add(new Answer(2, "Running water", false));
//        answers9.add(new Answer(3, "An electric tram", true));
//
//        questions.add(new Question(8, "What was Roundhay park the first to have in 1872?",
// answers9,
//                "It ran to what is now the car park and was the first of its kind in Britain.
// You" +
//                        " can still some of the Trolley poles."));
//
//        VenueActivity treasureHunt = new VenueActivity("Treasure Hunt", 0, 53.838846, -1.5014202,
//                treasureDetails);
//
//        treasureHunt.setDescription("Just like traditional treasure hunts the goal is to answer
// the question to win treasure, or in this case points. Merryn will ask the questions and you
// have to find the answers.");
//
//        treasureHunt.setGamePoints(1800);
//        treasureHunt.setRules(rules);
//        treasureHunt.setQuestions(questions);
//
//        venueActivities.add(treasureHunt);
//
//        VenueActivity physical = new VenueActivity("Physical", 1, 51.526721, -0.149832,
//                physicalDetails);
//        physical.setDescription("This game is a physical challenge. Digby will put 10 Sprytes
// out around the site and you have to find them. There’s a timer going and we have a league
// table for the fastest flag finders at each site. Get your skates on.");
//
//        venueActivities.add(physical);
////        venueActivities.add(new VenueActivity("Clues", 2,   51.528720, -0.155960,
/// cluesDetails));
////        venueActivities.add(new VenueActivity("Defuse the Bomb", 3, 51.532251, -0.161518,
/// defuseDetails));
////        venueActivities.add(new VenueActivity("Viewpoint", 4, 51.525824, -0.154814,
/// viewpointDetails));
//
//        shapeMarkers.add(new LocationBoundary(51.523764, -0.155430));
//        shapeMarkers.add(new LocationBoundary(51.529100, -0.167146));
//        shapeMarkers.add(new LocationBoundary(51.535419, -0.161557));
//        shapeMarkers.add(new LocationBoundary(51.536853, -0.153637));
//
//        shapeMarkers.add(new LocationBoundary(51.535766, -0.147600));
//        shapeMarkers.add(new LocationBoundary(51.530560, -0.145596));
//        shapeMarkers.add(new LocationBoundary(51.525292, -0.145756));
//
//        locationMarkers.add(new Amenity("toilet", Amenity.TOILET, 51.530954, -0.149846));
//        locationMarkers.add(new Amenity("cafe", Amenity.CAFE, 51.534494, -0.154405));
//        locationMarkers.add(new Amenity("playground", Amenity.PLAYGROUND, 51.530309, -0.161051));
//
//        //<!--    Roundhay Park    -->
//
//        List<Amenity> roundhayMarkers = new ArrayList<>();
//        roundhayMarkers.add(new Amenity("cafe", Amenity.CAFE,  53.83966,  -1.499723));
//        roundhayMarkers.add(new Amenity("cafe", Amenity.CAFE,   53.839572,   -1.504024));
//
//        List<LocationBoundary> roundhayShape = new ArrayList<>();
//        roundhayShape.add(new LocationBoundary(53.834255, -1.503511));
//        roundhayShape.add(new LocationBoundary(53.837802, -1.505151));
//        roundhayShape.add(new LocationBoundary(53.842094, -1.504143));
//        roundhayShape.add(new LocationBoundary(53.845002, -1.499902));
//        roundhayShape.add(new LocationBoundary(53.844409, -1.491318));
//        roundhayShape.add(new LocationBoundary(53.834363, -1.490586));
//
//        VenueActivity roundhayHunt = new VenueActivity("Treasure Hunt", 0, 53.814539, -0.807104,
//                treasureDetails);
//
//        roundhayHunt.setDescription("Just like traditional treasure hunts the goal is to answer
// the question to win treasure, or in this case points. Merryn will ask the questions and you
// have to find the answers.");
//        roundhayHunt.setGamePoints(1800);
//        roundhayHunt.setRules(rules);
//        roundhayHunt.setJsonQestionsFilename("questions/roundhay.json");
//
//        List<VenueActivity> roundhayVenueActivities = new ArrayList<>();
//        roundhayVenueActivities.add(roundhayHunt);
//
//        Location roundhayLocation = new Location(1, "Roundhay Park",1, 53.838846, -1.5014202,
// roundhayVenueActivities);
//        roundhayLocation.setBoundaries(roundhayShape);
//        roundhayLocation.setAmenities(roundhayMarkers);
//        //<!--    Shibden Hall    -->
//
//        List<Amenity> shibdenAmenities = new ArrayList<>();
//        shibdenAmenities.add(new Amenity("Cafe", Amenity.CAFE, 53.73064, -1.83737));
//        shibdenAmenities.add(new Amenity("Toilet", Amenity.TOILET, 53.73057, -1.83725));
//
//        List<LocationBoundary> shibdenShape = new ArrayList<>();
//        shibdenShape.add(new LocationBoundary(53.728610, -1.846400));
//        shibdenShape.add(new LocationBoundary(53.730057, -1.843826));
//        shibdenShape.add(new LocationBoundary(53.730514, -1.842023));
//        shibdenShape.add(new LocationBoundary(53.732139, -1.837023));
//        shibdenShape.add(new LocationBoundary(53.729359, -1.831831));
//        shibdenShape.add(new LocationBoundary(53.726947, -1.839191));
//        shibdenShape.add(new LocationBoundary(53.728724, -1.843354));
//        shibdenShape.add(new LocationBoundary(53.728610, -1.846400));
//
//        VenueActivity shibdenHunt = new VenueActivity("Treasure Hunt", 0, 53.814539, -0.807104,
//                treasureDetails);
//
//        shibdenHunt.setDescription("Just like traditional treasure hunts the goal is to answer
// the question to win treasure, or in this case points. Merryn will ask the questions and you
// have to find the answers.");
//        shibdenHunt.setGamePoints(1800);
//        shibdenHunt.setRules(rules);
//        shibdenHunt.setJsonQestionsFilename("questions/shibden.json");
//
//        List<VenueActivity> shibdenVenueActivities = new ArrayList<>();
//        shibdenVenueActivities.add(shibdenHunt);
//
//        Location shibdenLocation = new Location(2, "Shibden Hall",2, 53.7282663, -1.8399739,
// shibdenVenueActivities);
//        shibdenLocation.setBoundaries(shibdenShape);
//        shibdenLocation.setAmenities(shibdenAmenities);
//        shibdenLocation.setJsonPoiFilename("poi/shibden.json");
//
//        //<!--   Temple Newsham, Leeds    -->
//
//        List<Amenity> templeMarkers = new ArrayList<>();
//        templeMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.785247, -1.458294));
//
//        List<LocationBoundary> templeShape = new ArrayList<>();
//        templeShape.add(new LocationBoundary(53.927716, -0.776484));
//        templeShape.add(new LocationBoundary(53.928503, -0.774219));
//        templeShape.add(new LocationBoundary(53.926616, -0.772241));
//        templeShape.add(new LocationBoundary(53.926335, -0.770256));
//        templeShape.add(new LocationBoundary(53.925784, -0.771953));
//
//        Location templeLocation = new Location(3, "Temple Newsham, Leeds", 1, 53.7872738,
// -1.4700188, venueActivities);
//        templeLocation.setBoundaries(templeShape);
//        templeLocation.setAmenities(templeMarkers);
//        //<!--   Greenhead Park, Huddesfield    -->
//
//        List<Amenity> greenheadMarkers = new ArrayList<>();
//        greenheadMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.648072, -1.792659));
//
//        List<LocationBoundary> greenheadShape = new ArrayList<>();
//        greenheadShape.add(new LocationBoundary(53.648028, -1.799227));
//        greenheadShape.add(new LocationBoundary(53.650196, -1.800153));
//        greenheadShape.add(new LocationBoundary(53.650597, -1.799110));
//        greenheadShape.add(new LocationBoundary(53.648885, -1.794582));
//        greenheadShape.add(new LocationBoundary(53.649090, -1.793582));
//        greenheadShape.add(new LocationBoundary(53.647991, -1.791358));
//        greenheadShape.add(new LocationBoundary(53.646874, -1.796952));
//
//
//        Location greenheadLocation = new Location(4, "Greenhead Park, Huddesfield",1, 53.6492541,
//                -1.8007719, venueActivities);
//        greenheadLocation.setBoundaries(greenheadShape);
//        greenheadLocation.setAmenities(greenheadMarkers);
//
//        //<!--   Wollaton Park, Nottingham    -->
//
//        List<Amenity> wollatonMarkers = new ArrayList<>();
//        wollatonMarkers.add(new Amenity("Cafe", Amenity.CAFE, 52.9477, -1.21103));
//        wollatonMarkers.add(new Amenity("Toilet", Amenity.TOILET, 52.948, -1.21131));
//        wollatonMarkers.add(new Amenity("Car Park", Amenity.PARKING, 52.94881,	-1.21189));
//
//        List<LocationBoundary> wollatonShape = new ArrayList<>();
//        wollatonShape.add(new LocationBoundary(52.950095, -1.224430));
//        wollatonShape.add(new LocationBoundary(52.951834, -1.221584));
//        wollatonShape.add(new LocationBoundary(52.951438, -1.220870));
//        wollatonShape.add(new LocationBoundary(52.951695, -1.220303));
//        wollatonShape.add(new LocationBoundary(52.951230, -1.219314));
//        wollatonShape.add(new LocationBoundary(52.953603, -1.213187));
//        wollatonShape.add(new LocationBoundary(52.952153, -1.210956));
//        wollatonShape.add(new LocationBoundary(52.953618, -1.207562));
//        wollatonShape.add(new LocationBoundary(52.953177, -1.199909));
//        wollatonShape.add(new LocationBoundary(52.949265, -1.206862));
//        wollatonShape.add(new LocationBoundary(52.947599, -1.206727));
//        wollatonShape.add(new LocationBoundary(52.946174, -1.209639));
//        wollatonShape.add(new LocationBoundary(52.941608, -1.211286));
//        wollatonShape.add(new LocationBoundary(52.940628, -1.209547));
//        wollatonShape.add(new LocationBoundary(52.939248, -1.213742));
//        wollatonShape.add(new LocationBoundary(52.940428, -1.216494));
//        wollatonShape.add(new LocationBoundary(52.943451, -1.220217));
//        wollatonShape.add(new LocationBoundary(52.945079, -1.221724));
//        wollatonShape.add(new LocationBoundary(52.948832, -1.222932));
//        wollatonShape.add(new LocationBoundary(52.950053, -1.224188));
//
//        VenueActivity wolatonHunt = new VenueActivity("Treasure Hunt", 0, 52.9465224,-1.2289128,
//                treasureDetails);
//
//        wolatonHunt.setDescription("Just like traditional treasure hunts the goal is to answer " +
//                "the question to win treasure, or in this case points. Merryn will ask the " +
//                "questions and you have to find the answers.");
//        wolatonHunt.setGamePoints(1800);
//        wolatonHunt.setRules(rules);
//        wolatonHunt.setJsonQestionsFilename("questions/wolaton.json");
//
//        List<VenueActivity> wolatonVenueActivities = new ArrayList<>();
//        wolatonVenueActivities.add(wolatonHunt);
//
//        Location wollatonLocation = new Location(5, "Wollaton Park, Nottingham",1, 52.94819,
//                -1.20806, wolatonVenueActivities);
//        wollatonLocation.setBoundaries(wollatonShape);
//        wollatonLocation.setAmenities(wollatonMarkers);
//        //<!--   Bolton Abbey, Yorkshire    -->
//
//        List<Amenity> boltonMarkers = new ArrayList<>();
//        boltonMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.981538, -1.891104));
//
//        List<LocationBoundary> boltonShape = new ArrayList<>();
//        boltonShape.add(new LocationBoundary(53.981538, -1.891104));
//        boltonShape.add(new LocationBoundary(53.983632, -1.890571));
//        boltonShape.add(new LocationBoundary(53.985514, -1.884480));
//        boltonShape.add(new LocationBoundary(53.978445, -1.875994));
//
//        Location boltonLocation = new Location(6, "Bolton Abbey, Yorkshire",2, 53.9826605,
// -1.8885408, venueActivities);
//        boltonLocation.setBoundaries(boltonShape);
//        boltonLocation.setAmenities(boltonMarkers);
//
//        //<!--   Fletcher Moss Park, Manchester    -->
//
//        List<Amenity> fletcherMarkers = new ArrayList<>();
//        fletcherMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.408900, -2.229414));
//
//        Location fletcherLocation = new Location(7, "Fletcher Moss Park, Manchester",1,
// 53.4099252,
//                -2.2360764, venueActivities);
//        fletcherLocation.setAmenities(fletcherMarkers);
//
//        //<!--   Queens Park, Rochdale    -->
//
//        List<Amenity> queensMarkers = new ArrayList<>();
//        queensMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.599794, -2.220405));
//
//        List<LocationMarker> queensShape = new ArrayList<>();
//
//        Location queensLocation = new Location(8, "Queens Park, Rochdale",1, 53.6001373,
// -2.2181584, venueActivities);
//        queensLocation.setAmenities(queensMarkers);
//        //<!--   Alexandra Park, Oldham    -->
//
//        List<Amenity> alexandraMarkers = new ArrayList<>();
//        alexandraMarkers.add(new Amenity("cafe", Amenity.CAFE, 53.532298, -2.103277));
//
//        List<LocationMarker> alexandraShape = new ArrayList<>();
//
//        Location alexandraLocation = new Location(9, "Alexandra Park, Oldham",1, 53.5332412,
// -2.1072861, venueActivities);
//        alexandraLocation.setAmenities(alexandraMarkers);
//
//        List<LocationBoundary> cornerFramShape = new ArrayList<>();
//        cornerFramShape.add(new LocationBoundary(53.814539, -0.807104));
//        cornerFramShape.add(new LocationBoundary(53.814717, -0.804786));
//        cornerFramShape.add(new LocationBoundary(53.812170, -0.804711));
//        cornerFramShape.add(new LocationBoundary(53.812354, -0.807661));
//
//        List<Amenity> cornerFarmAmenities = new ArrayList<>();
//        cornerFarmAmenities.add(new Amenity("cafe", Amenity.CAFE, 53.814579, -0.805074));
//
//        VenueActivity farmHunt = new VenueActivity("Treasure Hunt", 0, 53.814539, -0.807104,
//                treasureDetails);
//
//        farmHunt.setDescription("Just like traditional treasure hunts the goal is to answer the
// question to win treasure, or in this case points. Merryn will ask the questions and you have
// to find the answers.");
//        farmHunt.setGamePoints(1800);
//        farmHunt.setRules(rules);
//        farmHunt.setJsonQestionsFilename("questions/farm.json");
//
//        List<VenueActivity> farmVenueActivities = new ArrayList<>();
//        farmVenueActivities.add(farmHunt);
//        Location cornerFarmLocation = new Location(10, "Corner Farm",1, 53.814539, -0.807104,
// farmVenueActivities);
//        cornerFarmLocation.setAmenities(cornerFarmAmenities);
//        cornerFarmLocation.setBoundaries(cornerFramShape);
//
//        //<!--   Heaton Park    -->
//        List<LocationBoundary> heatonShape = new ArrayList<>();
//        heatonShape.add(new LocationBoundary(53.529339788937,-2.2466665506363));
//        heatonShape.add(new LocationBoundary(53.534473235648,-2.2442847490311));
//        heatonShape.add(new LocationBoundary(53.535965325784,-2.2432011365891));
//        heatonShape.add(new LocationBoundary(53.537202317912,-2.2419673204422));
//        heatonShape.add(new LocationBoundary(53.539516798891,-2.2385662794113));
//        heatonShape.add(new LocationBoundary(53.540434905054,-2.239950299263));
//        heatonShape.add(new LocationBoundary(53.543941377287,-2.2472512722015));
//        heatonShape.add(new LocationBoundary(53.54450238589,-2.2512209415436));
//        heatonShape.add(new LocationBoundary(53.542819337774,-2.259932756424));
//        heatonShape.add(new LocationBoundary(53.538930220641,-2.2668206691742));
//        heatonShape.add(new LocationBoundary(53.535703894987,-2.2684729099274));
//        heatonShape.add(new LocationBoundary(53.533561376903,-2.2672498226166));
//        heatonShape.add(new LocationBoundary(53.532273263256,-2.2670352458954));
//        heatonShape.add(new LocationBoundary(53.530997864607,-2.26606965065));
//        heatonShape.add(new LocationBoundary(53.52740103352,-2.2658979892731));
//        heatonShape.add(new LocationBoundary(53.525895885886,-2.264803647995));
//        heatonShape.add(new LocationBoundary(53.524837148304,-2.2639667987823));
//        heatonShape.add(new LocationBoundary(53.527107661483,-2.2572290897369));
//
//        List<Amenity> heatonMarkers = new ArrayList<>();
//        heatonMarkers.add(new Amenity("Cafe", Amenity.CAFE, 53.53657904, -2.24433169));
//        heatonMarkers.add(new Amenity("Cafe", Amenity.CAFE, 53.53628574, -2.24335268));
//        heatonMarkers.add(new Amenity("Parking", Amenity.PARKING, 53.53654397, -2.24327222));
//        heatonMarkers.add(new Amenity("Cafe", Amenity.CAFE, 53.53606735, -2.25527242));
//        heatonMarkers.add(new Amenity("Toilet (unisex)", Amenity.TOILET, 53.53640370,
// -2.25536898));
//        heatonMarkers.add(new Amenity("Playground", Amenity.PLAYGROUND, 53.53747809,
// -2.25710437));
//        heatonMarkers.add(new Amenity("Parking", Amenity.PARKING, 53.53821930, -2.25729078));
//        heatonMarkers.add(new Amenity("Parking", Amenity.PARKING, 53.53820336, -2.26546079));
//        heatonMarkers.add(new Amenity("Parking", Amenity.PARKING, 53.52946415, -2.25408286));
//        heatonMarkers.add(new Amenity("Toilet (unisex)", Amenity.TOILET, 53.53084003,
// -2.25720897));
//        heatonMarkers.add(new Amenity("Cafe", Amenity.CAFE, 53.53108236, -2.25688443));
//        heatonMarkers.add(new Amenity("Playground", Amenity.PLAYGROUND, 53.53281370,
// -2.25949690));
//        heatonMarkers.add(new Amenity("Parking", Amenity.PARKING, 53.52629131, -2.26398289));
//
//        VenueActivity heatonHunt = new VenueActivity("Treasure Hunt", 0, 53.53385470, -2.25515842,
//                treasureDetails);
//
//        heatonHunt.setDescription("Just like traditional treasure hunts the goal is to answer
// the" +
//                " question to win treasure, or in this case points. Merryn will ask the
// questions and you have to find the answers.");
//        heatonHunt.setGamePoints(1800);
//        heatonHunt.setRules(rules);
//        heatonHunt.setJsonQestionsFilename("questions/heaton.json");
//
//        List<VenueActivity> heatonVenueActivities = new ArrayList<>();
//        heatonVenueActivities.add(heatonHunt);
//
//        Location heatonLocation = new Location(9, "Heaton Park",1, 53.53385470, -2.25515842,
// heatonVenueActivities);
//        heatonLocation.setBoundaries(heatonShape);
//        heatonLocation.setAmenities(heatonMarkers);
//
//        locations.add(heatonLocation);
//        locations.add(cornerFarmLocation);
//        locations.add(burnbyLocation);
//        locations.add(roundhayLocation);
//        locations.add(shibdenLocation);
//        locations.add(templeLocation);
//        locations.add(greenheadLocation);
//        locations.add(wollatonLocation);
//        locations.add(boltonLocation);
//        locations.add(fletcherLocation);
//        locations.add(queensLocation);
//        locations.add(alexandraLocation);
//
////        locations.add(new Location("Hyde Park & Playground", 0, 51.5072715, -0.167919, 3.5f,
/// 118, true, true, true, shapeMarkers, locationMarkers, venueActivities));
////        locations.add(new Location("The Regent's Park", 0, 51.531289, -0.156968, 2f, 95,
/// false, true, true, shapeMarkers, locationMarkers, venueActivities));
////        locations.add(new Location("St. James's Park", 0, 51.502495, -0.134818, 4.5f, 20,
/// true, true, false, shapeMarkers, locationMarkers, venueActivities));
////        locations.add(new Location("Buckingham Palace", 1, 51.501323, -0.1442207, 5, 350,
/// true, false, true, shapeMarkers, locationMarkers, venueActivities));
//
//        faqList.add(new Faq(0,"Who can play?", "The recommended age is from 3 to 103 :)
// Basically" +
//                " anyone can play, and we encourage everyone to download the app and try out
// the Augmented Reality brought in the real world by Sprytar."));
//        faqList.add(new Faq(1,"Are there any age restrictions?", "The players can be of all
// ages." +
//                " But we make sure that the content that children see is properly filtered,
// safe and approved by the main account, should the “Family” decide to create an account and
// have “Child” accounts."));
//        faqList.add(new Faq(2,"Where can Sprytar be played?", "Sprytar can be played in a large
// " +
//                "number of parks across the country. We make sure to increase that number week
// by week and hope to have the magical AR gameplay brought to over 1000 locations around UK by
// the end of 2017."));
//        faqList.add(new Faq(3,"What devices are supported?", "Sprytar is available for both
// major" +
//                " mobile platforms, iOS and Android. It can be played on any iOS device running
// iOS 8.0 or above, and any Android device running Android 4.0 or above."));
//        faqList.add(new Faq(4,"Do I have to sign up to play?", "No you do not, but we recommend
// " +
//                "that you do. The app works great even when not logged in, but the benefits of
// creating an account are great as you can save games, keep your earned badges and points, play
// offline and many more accolades. You can even choose to give it a go and after you are truly
// mesmerized by it, sign up right in the app and save all the initial progress that you made."));

    }

    @Inject
    public DummyRepository() {
    }

    @Override
    public Observable<List<Location>> getLocations() {
        return Observable.just(locations);
    }

    @Override
    public Observable<List<Faq>> getFaqData() {
        return Observable.just(faqList);
    }

    @Override
    public Observable<ArrayMap<String, Uri>> getAvatarList() {
        return Observable.just(avatarMap);
    }

    @Override
    public Observable<List<GameRule>> getGameRulesList() {
        return Observable.just(rules);
    }

    public List<VenueActivity> getHardcodedActivities() {
        return hardcodedActivities;
    }

}
