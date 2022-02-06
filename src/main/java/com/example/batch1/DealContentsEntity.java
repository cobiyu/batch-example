package com.example.batch1;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "deal_contents")
public class DealContentsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer seq;
	private String dealContentsName;
	private int columnCount;
	private int width;
}
