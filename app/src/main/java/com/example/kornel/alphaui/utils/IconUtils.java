package com.example.kornel.alphaui.utils;

import com.example.kornel.alphaui.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IconUtils {

    private static final Map<String, Integer> WORKOUTS_MAP = createMap();

    private static Map<String, Integer> createMap() {
        Map<String, Integer> result = new HashMap<>();

        // GPS
        result.put(GpsBasedWorkout.CYCLING.getValue(), R.drawable.ic_cycling_64);
        result.put(GpsBasedWorkout.ICE_SKATING.getValue(), R.drawable.ic_ice_skating_64_2);
        result.put(GpsBasedWorkout.RIDING.getValue(), R.drawable.ic_riding_64_2);
        result.put(GpsBasedWorkout.ROLLER_SKATING.getValue(), R.drawable.ic_roller_skating_64_2);
        result.put(GpsBasedWorkout.ROWING.getValue(), R.drawable.ic_rowing_64);
        result.put(GpsBasedWorkout.RUNNING.getValue(), R.drawable.ic_running_64_2);
        result.put(GpsBasedWorkout.SKATEBOARDING.getValue(), R.drawable.ic_skateboarding_64_2);
        result.put(GpsBasedWorkout.SKIING.getValue(), R.drawable.ic_skiing_64_2);
        result.put(GpsBasedWorkout.SNOWBOARDING.getValue(), R.drawable.ic_snowboarding_64_2);
        result.put(GpsBasedWorkout.SWIMMING.getValue(), R.drawable.ic_swimming_64_2);
        result.put(GpsBasedWorkout.TREKKING.getValue(), R.drawable.ic_trekking_64_2);
        result.put(GpsBasedWorkout.WALKING.getValue(), R.drawable.ic_walking_64);

        // NONGPS
        result.put(NonGpsBasedWorkout.AEROBICS.getValue(), R.drawable.ic_aerobic_64);
        result.put(NonGpsBasedWorkout.BADMINTON.getValue(), R.drawable.ic_badminton_64);
        result.put(NonGpsBasedWorkout.BASKETBALL.getValue(), R.drawable.ic_basketball_64);
        result.put(NonGpsBasedWorkout.BOXING.getValue(), R.drawable.ic_boxing_64);
        result.put(NonGpsBasedWorkout.CROSSFIT.getValue(), R.drawable.ic_crossfit_64);
        result.put(NonGpsBasedWorkout.DANCING.getValue(), R.drawable.ic_dancing_64);
        result.put(NonGpsBasedWorkout.ELLIPTICAL.getValue(), R.drawable.ic_elliptical_64);
        result.put(NonGpsBasedWorkout.GYMNASTICS.getValue(), R.drawable.ic_gymnastics_64);
        result.put(NonGpsBasedWorkout.SPINNING.getValue(), R.drawable.ic_spinning_64);
        result.put(NonGpsBasedWorkout.INDOOR_ROWING.getValue(), R.drawable.ic_indoor_rowing_64);
        result.put(NonGpsBasedWorkout.ROPE_JUMPING.getValue(), R.drawable.ic_jumping_rope_64);
        result.put(NonGpsBasedWorkout.SQUASH.getValue(), R.drawable.ic_squash_64);
        result.put(NonGpsBasedWorkout.YOGA.getValue(), R.drawable.ic_yoga_64);
        result.put(NonGpsBasedWorkout.WEIGHT_TRAINING.getValue(), R.drawable.ic_weight_training_64);
        result.put(NonGpsBasedWorkout.ZUMBA.getValue(), R.drawable.ic_zumba_64);
        return Collections.unmodifiableMap(result);
    }

    public static int getWorkoutIcon(String key) {
        return WORKOUTS_MAP.get(key);
    }
}
