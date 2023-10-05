import pandas as pd

ds = pd.read_csv("minsk-metro-dataset.csv")

scs = ""
for _, row in ds.iterrows():
    pattern = f"""
metro_station_{row["Station"].replace(" ", "_")}
    <- metro_station;
    <- rrel_station:
        metro_Minsk;
        metro_line_{row["Line"]};

    => nrel_main_idtf: [{row["StationRus"]}] (* <- lang_ru;; *);
    => nrel_main_idtf: [{row["Station"]}] (* <- lang_en;; *);

    => nrel_geoposition: ... (*
        -> rrel_longitude: [{row["Longitude"]}];;
        -> rrel_latitude: [{row["Latitude"]}];;
        <- WGS84
    *);;
    
"""
    scs += pattern

with open("metros.scs", "w", encoding="utf-8") as file:
    file.write(scs)
