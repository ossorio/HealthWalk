#!/usr/bin/env bash
if [ $# -eq 1 ]
then
    fichero=$1
else
    echo "Uso: ./geo.sh <fichero_con_direcciones> [campo_dirección]"
fi

output="coords.csv"
echo -n '' > $output
while read direccion
do
    dir=`echo $direccion | sed 's/ /\+/g'`
    request="http://maps.googleapis.com/maps/api/geocode/json?address=$dir"
    response=`curl "$request" 2> /dev/null`
    coords=`echo $response | jsawk "return this.results[0].geometry.location" 2> /dev/null`

    if [ ${#coords} == 0 ]
    then
        echo $dir
        echo "$dir" >> $output
        echo "$response"
    else
        lat=`echo $coords | jsawk "return this.lat"`
        lon=`echo $coords | jsawk "return this.lng"`
        echo "$lat;$lon" >> $output
    fi
done < $fichero
