# geocoding

An java api for sorting object by an reference point. This library was created to be fast otherwise the results has lost in precision.

This api can manage french department. 

## Usage

### Create an Point 

```
Point pt = Point.builder()
                    .lat(lattitude)
                    .lon(longitude)
                    .build(); 
```

The point is the base objet of this framework. This object can must contain lattitude, longtude and maybe an Object.

### Calculate the distance between to points

```

```

### Sorting object by distance between the data and an reference point

```
List<Datas> list =  new ArrayList();
list.add(data1);
list.add(data2);
...

Datas MyreferencePoint = ...

List<Datas> sortinglist = Distances.sorter(MyreferencePoint.getLatitude(), MyreferencePoint.getLongitude())
    .sort(list, d -> Point.builder(d)
                .lon(d.getLongitude())
                .lat(d.getLatitude())
                .build());

```

## License

[MIT](License.md)
