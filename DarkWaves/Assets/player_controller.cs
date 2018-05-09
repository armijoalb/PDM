using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class player_controller : MonoBehaviour {

	private Rigidbody rb;
	private Animation anim;
	private Joystick joystick;

	// Use this for initialization
	void Start () {
		rb = GetComponent<Rigidbody> ();
		anim = GetComponent<Animation> ();
		joystick = FindObjectOfType<Joystick> ();
	}
	
	// Update is called once per frame
	void Update () {
		float x = joystick.Horizontal;
		float y = joystick.Vertical;
		Vector3 movement = new Vector3 (-x, 0, -y);
		rb.velocity = movement;

		if(x != 0 && y != 0){
			transform.eulerAngles = new Vector3 (transform.eulerAngles.x, Mathf.Atan2 (x, y) * Mathf.Rad2Deg, transform.eulerAngles.z);
		}

		if (x != 0 || y != 0) {
			anim.Play ("walk");
		} else {
			anim.Play ("idle");
		}
	}
}
