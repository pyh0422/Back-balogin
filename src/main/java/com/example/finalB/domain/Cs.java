package com.example.finalB.domain;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Cs")
@SequenceGenerator(
		name = "CS_SEQ_GENERATOR", 
		sequenceName = "CS_SEQ", 
		initialValue = 1, allocationSize = 1)
@AllArgsConstructor
public class Cs {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "CS_SEQ_GENERATOR")
	private Integer no; // 게시글 번호

	@Column(nullable = false, length = 100)
	private String title; // 게시글 제목

	@Column(nullable = false, length = 1000)
	private String content; // 내용

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private Timestamp createDate; // 작성일자
	
	private int cnt; // 조회수

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "memberid")
	private Member member;


}




