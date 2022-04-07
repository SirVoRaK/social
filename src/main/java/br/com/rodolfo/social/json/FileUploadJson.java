package br.com.rodolfo.social.json;

public class FileUploadJson {
    private String name;
    private String path;

    public FileUploadJson() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileUploadJson{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
