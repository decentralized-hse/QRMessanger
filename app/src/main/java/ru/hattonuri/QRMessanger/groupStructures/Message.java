package ru.hattonuri.QRMessanger.groupStructures;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
public class Message extends RealmObject {
    @PrimaryKey @Getter @Setter
    private long date;

    @Getter @Setter
    private String dialer;

    @Getter @Setter
    private String text;

    @Getter @Setter
    private boolean received;
}
