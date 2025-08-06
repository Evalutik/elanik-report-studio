package com.example.models;

/**
 * Class used only for taking decoded values from database Results.MatchAlloysElements (As,
 * for some reason, it is encoded in the database in a single column). Class mirrors
 * C++ ElementPercentageFitStore structure from ElanikManager program's code.
 */
public record ElementPercentageFitStore(int ind, float min, float max, float fit, int commentId) {
}