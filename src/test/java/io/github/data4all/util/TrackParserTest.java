//package io.github.data4all.util;
//
//import io.github.data4all.model.data.Track;
//
//import java.io.File;
//import java.util.Date;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
//import android.location.Location;
//
///**
// * @author sbrede
// *
// */
//@RunWith(RobolectricTestRunner.class)
//@Config(emulateSdk = 18)
//public class TrackParserTest {
//
//    @Test
//    public void parseTest() {
//        Track track = new Track(Robolectric.shadowOf());
//
//        Location aloc = new Location("aloc");
//        Location bloc = new Location("bloc");
//        Location cloc = new Location("cloc");
//        Location[] locs = { aloc, bloc, cloc };
//        double startLat = 53.07929619999999;
//        double startLon = 8.801693699999987;
//
//        for (int i = 0; i < 3; i++) {
//            locs[i].setLatitude(startLat + i / 1000000000);
//            locs[i].setLongitude(startLon + i / 1000000000);
//            // loc.setAltitude(altitude);
//            locs[i].setTime(new Date().getTime() + i);
//            track.addTrackPoint(locs[i]);
//        }
//        
//        File expectedFile = new File(track.saveTrack());
//        Assert.assertTrue(expectedFile.exists());
//        expectedFile.delete();
//    }
//}
