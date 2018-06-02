using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class follow_player : MonoBehaviour {

	private Transform player;
	private Rigidbody rb;
	private float mummy_speed = 0.85f;

	// Use this for initialization
	void Start () {
		rb = GetComponent<Rigidbody> ();
		player = GameObject.FindGameObjectWithTag ("Player").transform;
	}
	
	// Update is called once per frame
	void FixedUpdate () {
		transform.LookAt (player);
		rb.velocity = transform.forward * mummy_speed;
	}
}
