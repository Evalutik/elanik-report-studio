package com.example.models;

/**
 * Class used only for taking decoded values from database Results.MatchAlloys (As,
 * for some reason, it is encoded in the database in a single column). Class mirrors
 * C++ TypeAlloyMatch structure from ElanikManager program's code.
 */
public record TypeAlloyMatch(int id, int nameId, int typeId, float fit, int elementsNum) {
}