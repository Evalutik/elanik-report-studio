package com.example.services;

import com.example.models.ElementData;
import java.util.*;

public class SortDataService {
    public static List<ElementData> sortElementsData(
            Map<Integer, ElementData> indexedElementsData,
            String baseElementName
    ){
        List<ElementData> sortedElementsData = new ArrayList<>();

        addBase(indexedElementsData.values(), sortedElementsData, baseElementName);
        addCarbon(indexedElementsData, sortedElementsData);
        addOtherElements(indexedElementsData, sortedElementsData, baseElementName);

        return sortedElementsData;
    }

    private static void addBase(Collection<ElementData> unsortedList, List<ElementData> out, String baseElementName){
        for (ElementData eData : unsortedList){
            if (eData.getName().equals(baseElementName)){
                out.add(eData);
                return;
            }
        }
    }

    private static void addCarbon(Map<Integer, ElementData> indexedElementsData, List<ElementData> out){
        ElementData carbonData = indexedElementsData.get(6);
        if (carbonData != null && carbonData.getConcentrationFloat() > 0f){
            out.add(carbonData);
        }
    }

    /**
     * Filters out:
     *  • any ElementData whose .getName() equals baseElementName
     *  • the entry under key==6 if its .getConcentration() > 0
     * Then sorts the remaining ElementData by descending concentration.
     *
     * @param indexedElementsData  map from element-index to ElementData
     * @param baseElementName      the element name to exclude
     * @param out                  list with output to add elements.
     */
    private static void addOtherElements(
            Map<Integer, ElementData> indexedElementsData,
            List<ElementData> out,
            String baseElementName
    ) {
        List<ElementData> otherSortedElements = indexedElementsData.entrySet().stream()
                // 1) filter out baseElementName
                .filter(e -> !e.getValue().getName().equals(baseElementName))
                // 2) filter out the special key=6 with conc>0
                .filter(e -> !(e.getKey() == 6 && e.getValue().getConcentrationFloat() > 0f))
                // 3) extract the ElementData
                .map(Map.Entry::getValue)
                // 4) sort by concentration descending
                .sorted(Comparator.comparing(ElementData::getConcentrationFloat).reversed())
                // 5) collect to list
                .toList();
        out.addAll(otherSortedElements);
    }
}
