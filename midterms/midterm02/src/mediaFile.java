public class mediaFile {

String mediaPath, GPS, date; //We store the required data from the given files
    public mediaFile(String PATH) {
    this.mediaPath = PATH;
    getData(PATH);
}

private void getData(String PATH) {
        GPS = new metaExtract().metaExtractGPS(PATH); //we exctract the GPS & Date data from the provided files
        date = new metaExtract().metaExtractDate(PATH);
}

}
