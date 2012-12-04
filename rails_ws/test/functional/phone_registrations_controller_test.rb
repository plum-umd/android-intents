require 'test_helper'

class PhoneRegistrationsControllerTest < ActionController::TestCase
  setup do
    @phone_registration = phone_registrations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:phone_registrations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create phone_registration" do
    assert_difference('PhoneRegistration.count') do
      post :create, phone_registration: { device_ident: @phone_registration.device_ident, registration_ident: @phone_registration.registration_ident }
    end

    assert_redirected_to phone_registration_path(assigns(:phone_registration))
  end

  test "should show phone_registration" do
    get :show, id: @phone_registration
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @phone_registration
    assert_response :success
  end

  test "should update phone_registration" do
    put :update, id: @phone_registration, phone_registration: { device_ident: @phone_registration.device_ident, registration_ident: @phone_registration.registration_ident }
    assert_redirected_to phone_registration_path(assigns(:phone_registration))
  end

  test "should destroy phone_registration" do
    assert_difference('PhoneRegistration.count', -1) do
      delete :destroy, id: @phone_registration
    end

    assert_redirected_to phone_registrations_path
  end
end
