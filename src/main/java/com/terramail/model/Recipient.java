package com.terramail.model;

import java.util.Objects;

/**
 * Represents an email recipient (To, Cc, Bcc, From).
 */
public class Recipient {
    private long id;
    private long messageId;
    private String email;
    private String name;
    private String type; // TO, CC, BCC, FROM

    public enum Type {
        TO("TO"),
        CC("CC"),
        BCC("BCC"),
        FROM("FROM");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type fromString(String type) {
            for (Type t : Type.values()) {
                if (t.value.equalsIgnoreCase(type)) {
                    return t;
                }
            }
            return TO;
        }
    }

    public Recipient() {
        this.id = 0;
        this.messageId = 0;
        this.email = "";
        this.name = "";
        this.type = "TO";
    }

    public Recipient(String email, String name, Type type) {
        this();
        this.email = email;
        this.name = name;
        this.type = type.getValue();
    }

    public Recipient(String email, Type type) {
        this(email, "", type);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayString() {
        if (name != null && !name.isEmpty()) {
            return String.format("\"%s\" <%s>", name, email);
        }
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipient recipient = (Recipient) o;
        return email.equals(recipient.email) && type.equals(recipient.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, type);
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
