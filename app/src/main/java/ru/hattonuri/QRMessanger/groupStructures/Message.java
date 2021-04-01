package ru.hattonuri.QRMessanger.groupStructures;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Message extends RealmObject {
    @PrimaryKey @Getter @Setter
    private Date date;

    @PrimaryKey @Getter @Setter
    private String dialer;

    @Getter @Setter
    private String text;

    @Getter @Setter
    private boolean received = true;
}
