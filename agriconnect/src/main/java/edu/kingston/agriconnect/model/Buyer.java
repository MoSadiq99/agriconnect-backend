package edu.kingston.agriconnect.model;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@DiscriminatorValue("BUYER")
public class Buyer extends User implements Serializable {  // TODO: want to learn Serializable

}
